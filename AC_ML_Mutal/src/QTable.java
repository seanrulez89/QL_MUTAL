import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bwapi.GameType;
import bwapi.UnitType;

public class QTable extends HashMap<QState, Map<QAction, Double>> implements Serializable {
	
	private static final long serialVersionUID = 4952624763235942586L;
	private static QTable instance;
	
	public static QTable getInstance() {
		if (instance == null) {
			instance = new QTable();
		}
		return instance;
	}
	
	public QTable() {
		
	}
	
	/**
	 * 현재상태에서 다음상태의 Action에 reward를 Qtable에 저장한다.
	 * @param crtStat
	 * @param nextAction
	 * @param value
	 */
	public void setQ(QState crtStat, QAction nextAction, double value) {
		Map<QAction, Double> actionMap = this.get(crtStat);
		if (actionMap == null) {
			actionMap = new HashMap<QAction, Double>();
		}
		actionMap.put(nextAction, value);
		this.put(crtStat, actionMap);
	}

	/**
	 * 현재상태에서 다음상태의 reward값을 리턴한다.
	 * @param crtState
	 * @param nextAction
	 * @return reward
	 */
	public double getQ(QState crtState, QAction nextAction) {
		if (this.get(crtState) == null) {
			return 0.;
		}
		if (this.get(crtState).get(nextAction) == null) {
			return 0.;
		} 
			
		return this.get(crtState).get(nextAction);
	}

	/**
	 * 현재상태에서 q값이 가장큰 Action을 리턴한다.
	 * @param crtState
	 * @return QAction
	 */
	public List<QAction> maxQActionsFromState(QState crtState) {
		List<QAction> actionsFromCurrentState;
		Map<QAction, Double> crtStateMap = this.get(crtState);
		
		/*
		 * 학습되지 않은 상태인 경우 적군이 없는 격자로 이동한다.
		 */
		if (!QUtil.isTraningState(crtStateMap)) {

			//actionsFromCurrentState = crtState.possibleActionsFromState();
			int grdIdx = crtState.getMinAttackUnitGrdIdx(crtState.getGrdList());
			actionsFromCurrentState = new ArrayList<QAction>();
			// Move 액션
			QAction moveAction = crtState.getMoveActionWidGrdIdx(grdIdx);
			actionsFromCurrentState.add(moveAction);
		
		} else {
			actionsFromCurrentState = new ArrayList<QAction>();
			double max = Double.NEGATIVE_INFINITY;
			QAction ret = null;
			for (QAction action : crtStateMap.keySet()) {
				if (action.getActionType() == null) {
					continue;
				}
				if (action.getActionType().equals(QConstants.ActionType.Move)) {
					int idx = action.getMoveGrdIdx();
					if (crtState.isUnreachableArea(idx)) {
						continue;
					}
				}
				
				if (crtStateMap.get(action) > max) {
					max = crtStateMap.get(action);
					ret = action;
				}
			}
			
//			if (ret == null) {
//				// Use Map 에서는 랜덤으로 학습하고 실전투에서는 마린이 적은 그리드로 이동한다.
//				int grdIdx = crtState.getMinAttackUnitGrdIdx(crtState.getGrdList());
//				// Move 액션
//				ret = new QAction();
//				ret.setActionType(QConstants.ActionType.Move);
//				ret.setMoveGrdIdx(grdIdx);
//				ret.setMoveX(crtState.getGrdList().get(grdIdx-1).getMovePositionX());
//				ret.setMoveY(crtState.getGrdList().get(grdIdx-1).getMovePositionY());
//				ret.setTargetUnitType("moveAction");
//				
//			} else {
//
//			}
			
			/**
			 * Action타입에 따라 현재상태를 maxQ Action에 반영한다.
			 * maxQ Action에는 이전 저장시 정보가 들어 있으므로..
			 */
			ret = crtState.setAttackMaxQAction(ret);
			actionsFromCurrentState.add(ret);
		}
		
		
		return actionsFromCurrentState;
	}
	
	/**
	 * 다음 상태의 max Q value를 리턴한다.
	 * @param nextState
	 * @return maxQ value
	 */
	public double maxQvForState(QState nextState) {
		Map<QAction, Double> nextStateMap = this.get(nextState);
		if (nextStateMap == null) {
			return 0.;
		}
		double max = Double.NEGATIVE_INFINITY;
		for (QAction qAction : nextStateMap.keySet()) {
			double value = nextStateMap.get(qAction);
			if (value > max) {
				max = value;
			}
		}
		return max;
	}
	
	@Override
	public Map<QAction, Double> get(Object o) {
		
		QState otherObj = null;
		
        if (o instanceof QStateZ) {
        	otherObj = (QStateZ) o;
		} else if (o instanceof QStateT) {
			otherObj = (QStateT) o;
		} else if (o instanceof QStateP) {
			otherObj = (QStateP) o;
		}
        
        for(QState thisObj : this.keySet()){
        	if (otherObj.equals(thisObj)) {
				return (Map<QAction, Double>) super.get(thisObj);
			}
        }
		return null;
	}

	@Override
	public Map<QAction, Double> put(QState crtStat, Map<QAction, Double> value) {
        for(QState s : this.keySet()){
        	if (s.equals(crtStat)) {
				this.remove(s);
				break;
			}
        }
        return super.put(crtStat, value);
	}
	
	public void setInstance(Object readObject) {
		instance = (QTable) readObject;
	}
}
