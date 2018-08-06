import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bwapi.Color;
import bwapi.Player;
import bwapi.Position;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Polygon;

public class QUtil {
	
	/**
	 * Action을 수행한다.
	 * @param myUnitList
	 * @param actionsFromCurrentState
	 * @return
	 */
	public static QAction actActionRandom(QUnitList myUnitList, List<QAction> actionsFromCurrentState) {
		
		boolean isAttack = false;
		QAction action;
		
		// 사이즈가 1인경우 maxQ로 실행
		if (actionsFromCurrentState.size() < 2) {
			//System.out.println("actionsFromCurrentState.size() < 2");
			action = actMaxQ(myUnitList, actionsFromCurrentState.get(0));
		} else {
			// 랜덤으로 공격 또는 후퇴
			Random rd = new Random();
			int rdIdx = rd.nextInt(2);
			// 0이면 공격
			//System.out.println("actionsFromCurrentState.size() : " + actionsFromCurrentState.size());
			if (rdIdx == 0 && actionsFromCurrentState.size() == QConstants.RANDOM_ACTION_CNT) {
				isAttack = true;
			}
			
			action = actRandom(myUnitList, actionsFromCurrentState, isAttack);
		}
		return action;
	}
	
	/**
	 * 랜덤하게 Action을 수행한다.
	 * @param myUnitList
	 * @param actionsFromCurrentState
	 * @param isAttack
	 * @return
	 */
	private static QAction actRandom(QUnitList myUnitList, List<QAction> actionsFromCurrentState, boolean isAttack) {
		//System.out.println("actRandom 1");
		QAction qAction = null;
		// 공격인 경우
		if (isAttack) {
			for (QAction action : actionsFromCurrentState) {
				if (QConstants.ActionType.Attack.equals(action.getActionType())) {
					//System.out.println("actRandom 2");
	    			// 공격
	    			Unit targetUnit = getUnit(action.getTargetId());
	    			//System.out.println("actRandom 3");
	    			myUnitList.rightClick(targetUnit);
	    			qAction = action;
	    			//System.out.println("actRandom 4");
	    			// 사용자 화면에 표시하기 위한 플래그처리
	    			QFlag.setTargetUnit(targetUnit);
	    			break;
				}
			}
		} else {
			// Move인 경우
			
			// 랜덤하게 이동할 경로 선택
			Random rd = new Random();
			int rdIdx = rd.nextInt(actionsFromCurrentState.size()-1);
			//System.out.println("actRandom 7");
			QAction action = actionsFromCurrentState.get(rdIdx);
            Position ps = new Position(action.getMoveX(), action.getMoveY());
			myUnitList.rightClick(ps);
			//System.out.println("actRandom 8");
			// 사용자 화면에 표시하기 위한 플래그처리
			QFlag.setMovingTargetGrdIdx(action.getMoveGrdIdx());
			qAction = action;
		}
		
		return qAction;
	}

	/**
	 * 파라미터로 입력받은 유닛ID의 BWAPI유닛 객체를 리턴한다.
	 * @param unitId
	 * @return
	 */
	private static Unit getUnit(int unitId) {
		Player enemy = MyBotModule.Broodwar.enemy();
		Unit rtnEnemyUnit = null;
		for (Unit enemyUnit : enemy.getUnits()) {
			if (enemyUnit.getID() == unitId) {
				rtnEnemyUnit = enemyUnit;
				break;
			}
		}
		return rtnEnemyUnit;
	}

	/**
	 * Action을 수행한다.
	 * @param myUnitList
	 * @param qAction
	 * @return
	 */
	private static QAction actMaxQ(QUnitList myUnitList, QAction qAction) {
		//System.out.println("actMaxQ 1");
		if (QConstants.ActionType.Attack.equals(qAction.getActionType())) {
			// 공격
			//System.out.println("actMaxQ 2");
			Unit targetUnit = getUnit(qAction.getTargetId());
			//System.out.println("actMaxQ 2 - 1");
			if (targetUnit != null) {
				myUnitList.rightClick(targetUnit);
				//System.out.println("actMaxQ 3");
				
				// 사용자 화면에 표시하기 위한 플래그처리
				QFlag.setTargetUnit(targetUnit);
			}

		} else {
			//System.out.println("actMaxQ 4");
			// 이동
            Position ps = new Position(qAction.getMoveX(), qAction.getMoveY());
			myUnitList.rightClick(ps);
			
			//System.out.println("actMaxQ 4");
			// 사용자 화면에 표시하기 위한 플래그처리
			QFlag.setMovingTargetGrdIdx(qAction.getMoveGrdIdx());
		}
		return qAction;
	}

	/**
	 * Action이 종료되었는지 여부를 리턴한다.
	 * @param crtStat
	 * @param nextState
	 * @param qAction
	 * @return
	 */
	public static boolean isActDone(QState crtStat, QState nextState, QAction action) {
		
		boolean isActionEnd = false;
		
		if (action.getActionType().equals(QConstants.ActionType.Attack)) {
			
			QState thisState = crtStat;
			QState otherState = nextState;
			
			// 이동중 나를 타격할 수 있는 유닛이 3이상이면 다시 판단한다.
			/*
			 *   && !QFlag.isTraningMode()
			 */
			if (nextState.getEneAttackCnt() > 1) {
				int newAttackUnitCnt = 0;
				boolean isNewAttackUnit = true;
				List<Integer> eneList = crtStat.getEneList();
				List<Integer> eneAttackList = nextState.getEneAttackList();
				
				for (int j = 0; j < eneAttackList.size(); j++) {
					isNewAttackUnit = true;
					for (int i = 0; i < eneList.size(); i++) {
						if (eneList.get(i) - eneAttackList.get(j) == 0) {
							isNewAttackUnit = false;
							break;
						}
					}
					
					if (isNewAttackUnit) {
						newAttackUnitCnt++;
					}
				}

				if (newAttackUnitCnt > 1) {
					System.out.println("======== action.getActionType().equals(QConstants.ActionType.Attack)");
					return true;
				}
			}
			
			//System.out.println("isActDone 1");
			
			/*
			 * 공격할 유닛 변경되었다면 id를 반영한다.
			 */
			if (action.getTargetId() != nextState.getTargetUnitId()) {
				
				//System.out.println("nextState.getTargetUnitId() : " + nextState.getTargetUnitId());
				
    			// 공격
    			Unit nextTargetUnit = getUnit(nextState.getTargetUnitId());
    			if (nextTargetUnit != null) {
        			action.setTargetId(nextState.getTargetUnitId());
        			QFlag.getMyQUnits().rightClick(nextTargetUnit);
        			QFlag.setTargetUnit(nextTargetUnit);
				}
			}
			
			/*
			 * 공격할 유닛이 사정거리를 벗어난 경우 리턴
			 */
			if (getUnit(crtStat.getTargetUnitId()) == null) {
				return true;
			}
			
			//System.out.println("isActDone 2");
			
			/*
			 * 공격대기가 40을 초과할 경우 리턴
			 * 뮤탈 1마리의 cooldown 30
			 */
			int thisCooldown = thisState.getCooldown();
			int otherCooldown = otherState.getCooldown();
			
			//System.out.println("isActDone 3");
			
			if (Math.abs(thisCooldown - otherCooldown) > 30) {
				return true;
			}
			

//			int attackRange = QConstants.QMUTAL_GROUNDWEAPON_ATTCK_RANGE + 25;
//			
//			//System.out.println("isActDone 4");
//			
//			int unitDistance = QFlag.getMyUnit().getDistance(getUnit(crtState.getTargetUnitId()));
//			if (attackRange < unitDistance) {
//				return true;
//			}
		}

		// 이동인경우
		if (action.getActionType().equals(QConstants.ActionType.Move)) {

			// 이동중 나를 타격할 수 있는 유닛이 2이상이면 다시 판단한다.
			//   && !QFlag.isTraningMode()
			if (nextState.getEneAttackCnt() > 1) {
				int newAttackUnitCnt = 0;
				boolean isNewAttackUnit = true;
				List<Integer> eneList = crtStat.getEneList();
				List<Integer> eneAttackList = nextState.getEneAttackList();
				
				for (int j = 0; j < eneAttackList.size(); j++) {
					isNewAttackUnit = true;
					for (int i = 0; i < eneList.size(); i++) {
						if (eneList.get(i) - eneAttackList.get(j) == 0) {
							isNewAttackUnit = false;
							break;
						}
					}
					
					if (isNewAttackUnit) {
						newAttackUnitCnt++;
					}
				}

				if (newAttackUnitCnt > 1) {
					System.out.println("======== action.getActionType().equals(QConstants.ActionType.Move)");
					return true;
				}
			}
			
			// 이동인 경우 타겟지점과 현재 유닛의 지점의 거리차이에 도달한 경우로 판단한다.
			Position targetPosition = new Position(action.getMoveX(), action.getMoveY());
			Position myUnitPosition = QFlag.getMyUnit().getPosition();
			double distance = myUnitPosition.getDistance(targetPosition);
			if (distance < QConstants.MOVE_ACTION_DISTANCE) {
				return true;
			} else {
				QFlag.getMyQUnits().rightClick(targetPosition);
			}
		}
		return isActionEnd;
	}
	
	/**
	 * Reward를 계산하여 리턴한다.
	 * @param crtStat
	 * @param nextState
	 * @param qAction
	 * @return
	 */
	public static double getReward(QState crtStat, QState nextState, QAction qAction) {
		double reward = 0.;
		
		// 아군의 체력차이
		double myHpDiff = crtStat.getMyHitPoints() - nextState.getMyHitPoints();
		
		// 저그는 시간에 따라 체력이 회복되므로 회복되는 체력은 reward에서 제외
		if (nextState.getMyHitPoints() > crtStat.getMyHitPoints()) {
			myHpDiff = 0.;
		}
		
		// 공격인 경우 적 유닛에 따라 보상으로 주고 공격가능유닛수로 페널티를 준다.
		int killPize = 0;
		int penalty = 0;
		if (QConstants.ActionType.Attack.equals(qAction.getActionType())) {
			if (qAction.getTargetUnitType().equals(UnitType.Terran_Marine+"")) {
				killPize = 40;
			} else if (qAction.getTargetUnitType().equals(UnitType.Terran_Missile_Turret+"")) {
				killPize = 70;
			} else {
				killPize = 40;
			}
			
			penalty = (int) Math.pow(2, nextState.getEneAttackCnt());
		}
		
		// 리워드 = 공격보상 - 아군체력소모 - 적군공격가능유닛수페널티
		reward = killPize - myHpDiff - penalty;

		return reward;
	}
	
	/**
	 * 사용자 화면에 Q유닛의 정보를 표시한다.
	 * @param unit
	 */
	public static void drawUnitViewMap(Unit unit) {
		
		int sightRange = QConstants.QMUTAL_SINGHT_RANGE; // 224
		int attackRange = QConstants.QMUTAL_GROUNDWEAPON_ATTCK_RANGE;
		int divideCnt = QConstants.SINGHT_DIVIDE_CNT;
		int cellSize = sightRange / divideCnt + 10;
		
		int cellCenterInitX = unit.getPosition().getX() - cellSize*divideCnt;
		int cellCenterInitY = unit.getPosition().getY() - cellSize*divideCnt;
		
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), sightRange, Color.Cyan);
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), attackRange, Color.Red);
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), attackRange, Color.Red);
		
		if (QFlag.getTargetUnit() != null && QFlag.getNextAction() !=null &&  QConstants.ActionType.Attack.equals(QFlag.getNextAction().getActionType())) {
			MyBotModule.Broodwar.drawCircleMap(QFlag.getTargetUnit().getPosition(), 10, Color.Red);
		}
		
		int idx = 0;
		int cellCenterXL = cellCenterInitX;
		int cellCenterYD = cellCenterInitY;
		for (int i = 0; i < divideCnt*2; i++) {
			int cellCenterYU = cellCenterYD + cellSize;
			for (int j = 0; j < divideCnt*2; j++) {
				int cellCenterXR = cellCenterXL + cellSize;
				MyBotModule.Broodwar.drawBoxMap(cellCenterXL, cellCenterYD, cellCenterXR, cellCenterYU, Color.Orange);
				int x = (cellCenterXL + cellCenterXR) / 2;
				int y = (cellCenterYD + cellCenterYU) / 2;
				idx++;
				
				MyBotModule.Broodwar.drawTextMap(x, y, ""+idx);

				if (QFlag.getNextAction() !=null && idx == QFlag.getMovingTargetGrdIdx() && QConstants.ActionType.Move.equals(QFlag.getNextAction().getActionType())) {
					MyBotModule.Broodwar.drawBox(bwapi.CoordinateType.Enum.Map, cellCenterXL, cellCenterYD, cellCenterXR, cellCenterYU, Color.Yellow, true);
				}
				cellCenterXL = cellCenterXL + cellSize;
				
			}
			cellCenterYD = cellCenterYD + cellSize;
			cellCenterXL = cellCenterInitX;
		}
	}
	
	/**
	 * 사용자 화면에 Q유닛의 정보를 표시한다.
	 * @param unit
	 */
	public static void drawUnitViewMap2(Unit unit) {
		
		int sightRange = QConstants.QMUTAL_SINGHT_RANGE;
		int attackRange = QConstants.QMUTAL_GROUNDWEAPON_ATTCK_RANGE;
		
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), sightRange, Color.Cyan);
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), attackRange, Color.Red);
		MyBotModule.Broodwar.drawCircleMap(unit.getPosition(), attackRange, Color.Red);
		
		if (QFlag.getTargetUnit() != null && QFlag.getNextAction() !=null &&  QConstants.ActionType.Attack.equals(QFlag.getNextAction().getActionType())) {
			MyBotModule.Broodwar.drawCircleMap(QFlag.getTargetUnit().getPosition(), 10, Color.Red);
		}
		
		int sightRange2 = sightRange * 2;
		int divideCnt = 3;
		int cellSize = sightRange2 / divideCnt;
		int initXL = unit.getPosition().getX() - sightRange;
		int initYD = unit.getPosition().getY() - sightRange;
		int idx = 0;
		
		for (int i = 0; i < divideCnt; i++) {
			int xL = initXL;
			int xR = 0;
			int yU = 0;
			int yD = initYD + i*cellSize;
			for (int j = 0; j < divideCnt; j++) {
				xR = xL + cellSize;
				yU = yD + cellSize;
				MyBotModule.Broodwar.drawBoxMap(xL, yD, xR, yU, Color.Orange);
				int x = (xL + xR) / 2;
				int y = (yU + yD) / 2;
				idx++;
				
				MyBotModule.Broodwar.drawTextMap(x, y, ""+idx);

				if (QFlag.getNextAction() !=null && idx == QFlag.getMovingTargetGrdIdx() && QConstants.ActionType.Move.equals(QFlag.getNextAction().getActionType())) {
					MyBotModule.Broodwar.drawBox(bwapi.CoordinateType.Enum.Map, xL, yD, xR, yU, Color.Yellow, true);
				}
				xL = xR;
			}
		}
	}
	/**
	 * Q Table java객체 직렬화 -  파일쓰기
	 * @param race 
	 */
	public static void saveFileToQTable(String race) {
		try {
			String fileNm = "qtable" +  race + ".dat";
			FileOutputStream fos = new FileOutputStream(fileNm);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(QTable.getInstance());
			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Q Table java객체 직렬화 -  파일읽기
	 * @param race 
	 */
	public static void readQTableFile(String race) {
		try {
			String fileNm = "qtable" +  race + ".dat";
			FileInputStream fis = new FileInputStream(fileNm);
			ObjectInputStream ois = new ObjectInputStream(fis);
			QTable.getInstance().setInstance(ois.readObject());
			ois.close();
			fis.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Q table에 reward를 Update한다.
	 * @param qTable
	 * @param crtStat
	 * @param nextState
	 * @param nextAction
	 * @return
	 */
	public static double updateQTable(QTable qTable, QState crtStat, QState nextState, QAction nextAction) {
		// 다음상태의 Max Q값을 취득한다.
		double nextMaxQ = qTable.maxQvForState(nextState);
		
		// Reward를 계산한다.
		double reward = getReward(crtStat, nextState, nextAction);
		
		// 현재 상태에서의 이전 Q값을 취득한다.
		double preQ = QFlag.getQ();
		
		// Q값 계산
		double qv = reward + QConstants.ALPHA * (preQ + QConstants.GAMMA * nextMaxQ - reward);
		
		// Q 테이블에 Q값을 반영한다.
		qTable.setQ(crtStat, nextAction, qv);
		
		QFlag.setNextAction(null);
		if (nextAction.getActionType().equals(QConstants.ActionType.Attack)) {
			System.out.println("*** updateQTable : " + nextAction.getActionType() + ", maxQ : " + nextMaxQ + ", reward : " + reward + ", value : " + qv + ", getMoveGrdIdx : " + nextAction.getMoveGrdIdx());
		}
		return qv;
	}

	/**
	 * 
	 * @param position
	 * @param targetPosition
	 * @return
	 */
	public static boolean isNearByPostion(Position position, Position targetPosition) {
		if (Math.abs(position.getX() - targetPosition.getX()) < 100 && Math.abs(position.getY() - targetPosition.getY()) < 100) {
			return true;
		}
		return false;
	}

	public static Position getTargetPosition() {
    	// 적군 본진 타겟 위치
		BaseLocation enemyBaseLocation = InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer);
		Chokepoint enemySecondChokePoint = InformationManager.Instance().getSecondChokePoint(MyBotModule.Broodwar.enemy());
		Position targetPosition = null;
		if (enemySecondChokePoint != null) {
			targetPosition = enemySecondChokePoint.getPoint();
		}
		
		if (enemyBaseLocation != null) {
			targetPosition = enemyBaseLocation.getPosition();
		}
		return targetPosition;
	}

	public static Chokepoint getTargetClusteringPosition() {
		// 뮤탈 뭉치기 위치
		Chokepoint secondChokePoint = BWTA.getNearestChokepoint(InformationManager.Instance().getSecondChokePoint(InformationManager.Instance().selfPlayer).getCenter());
		return secondChokePoint;
	}

	public static boolean isWithdrowMode() {
		return (MyBotModule.Broodwar.self().completedUnitCount(UnitType.Zerg_Mutalisk) > 0 && MyBotModule.Broodwar.self().completedUnitCount(UnitType.Zerg_Mutalisk) < 9);
	}

	public static boolean isUnreachablePosition(int movePositionX, int movePositionY) {
		
		//System.out.println("movePositionX : " + movePositionX + ", movePositionY : " + movePositionY);
		if (movePositionX < 50 || movePositionY < 50) {
			return true;
		}
		
		int mapWidth = MyBotModule.Broodwar.mapHeight() * 32 - 50;
		int mapHeight = MyBotModule.Broodwar.mapHeight() * 32 - 50;
		
		if (movePositionX > mapWidth || movePositionY > mapHeight) {
			return true;
		}
		
		return false;
	}

	public static boolean isDivided(Unit myUnit, QUnitList myUnitList) {
		int unitDistance = Integer.MAX_VALUE;
		for (Unit unit : myUnitList) {
			unitDistance = myUnit.getDistance(unit);
			System.out.println("isDivided unitDistance : " + unitDistance);
			if (unitDistance < 2000 && unitDistance > 700) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 파라미터로 입력받은 상태가 학습된 상태인지 여부를 리턴한다.
	 * 전체 학습 Action 17
	 * 학습 기준 Action 13
	 * @param crtStateMap
	 * @return
	 */
	public static boolean isTraningState(Map<QAction, Double> crtStateMap) {
		
		if (crtStateMap == null) {
			System.out.println("isTraningState crtStateMap == null");
			return false;
		}
		
		int actionSize = 0;
		Iterator<QAction> keys = crtStateMap.keySet().iterator();
        while( keys.hasNext() ){
        	QAction qAction = keys.next();
        	if (qAction.getActionType().equals(QConstants.ActionType.Attack) || qAction.getActionType().equals(QConstants.ActionType.Move)) {
        		actionSize++;
			}
        }
        System.out.println("isTraningState actionSize : " + actionSize);
        if (actionSize > 6) {
        	return true;
		} else {
			return false;
		}
	}

	/**
	 * 현재 상태를 리턴한다.
	 * 모든 종족에 대해 Q봇을 만들고자 하면 아래의 상태를 모두 정의하면 된다.
	 * (테란에 대해서만 정의되어 있음)
	 * @return
	 */
	public static QState getState() {
		
		QState state = null;
		if (MyBotModule.Broodwar.enemy().getRace() == Race.Terran) {
			state = new QStateT(MyBotModule.Broodwar);
		} else if (MyBotModule.Broodwar.enemy().getRace() == Race.Protoss) {
			state = new QStateP(MyBotModule.Broodwar);
		} else if (MyBotModule.Broodwar.enemy().getRace() == Race.Zerg) {
			state = new QStateZ(MyBotModule.Broodwar);
		}
		return state;
	}
	
	/**
	 * 전투가 가능한 상태인지를 리턴한다.
	 * @param targetPositionList
	 * @return
	 */
	public static boolean isCombatMode(List<Position> targetPositionList) {
		
		// 타겟 확인
		if (targetPositionList.isEmpty()) {
			return false;
		}
		
		// 초기화 완료여부 확인
		if (!QFlag.isInitComplete()) {
			return false;
		}
		
		// 유닛준비여부 확인
		if (QFlag.getMyQUnits().size() < QConstants.Q_LEARNING_START_UNIT_CNT) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 공격 및 도망치는 포인트를 생성한다.
	 * 적군의 베이스 및 확장기지 주변에 포인트를 생성한다.
	 * @param targetPositionList 
	 * @return
	 */
	public static void makeTargetPositionList(List<Position> targetPositionList) {
		
		BaseLocation eneBaseLocation = InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer);
		BaseLocation eneFirstExpansionLocation = InformationManager.Instance().getFirstExpansionLocation(InformationManager.Instance().enemyPlayer);
		
		if (eneBaseLocation == null || eneFirstExpansionLocation == null) {
			return;
		}
		
		Position eneBasePosition = InformationManager.Instance().getMainBaseLocation(InformationManager.Instance().enemyPlayer).getPosition();
		Position eneFirstExpansionPosition = InformationManager.Instance().getFirstExpansionLocation(InformationManager.Instance().enemyPlayer).getPosition();
		
		if (targetPositionList.isEmpty()) {
			
			List<Position> basePositionList = getLocationPositionList(eneBaseLocation);
			List<Position> FirstExpansionPositionList = getLocationPositionList(eneFirstExpansionLocation);
			
			for (int i = 0; i < 100; i++) {
				targetPositionList.add(FirstExpansionPositionList.get(0));
				for (int j = 1; j < FirstExpansionPositionList.size(); j++) {
					targetPositionList.add(eneFirstExpansionPosition);
					targetPositionList.add(FirstExpansionPositionList.get(j));
				}
				targetPositionList.add(basePositionList.get(0));
				for (int j = 1; j < basePositionList.size(); j++) {
					targetPositionList.add(eneBasePosition);
					targetPositionList.add(basePositionList.get(j));
				}
			}
		}
	}

	/**
	 * 해당되는 로케이션에 포인츠를 반환한다.
	 * @param baseLocation
	 * @return
	 */
	private static List<Position> getLocationPositionList(BaseLocation baseLocation) {
		
		List<Position> retList = new ArrayList<Position>();
		Polygon p = baseLocation.getRegion().getPolygon();
		for (int j = 0; j<p.getPoints().size(); j++)
		{
			if (j % 3 == 0) {
				retList.add(p.getPoints().get((j + 1) % p.getPoints().size()));
			}
		}
		return retList;
	}
}
