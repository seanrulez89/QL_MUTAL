import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class QStateT extends QState implements Serializable {
	
	private static final long serialVersionUID = -6553390848404649963L;
	
	/**
	 * Q유직의 시야 그리드 격자
	 */
	private List<Grid> grdList;
	
	/**
	 * 아군 Q유닛의 HP 총합
	 */
	private int myHitPoints;
	
	/**
	 * 아군 Q유닛의 cooldown
	 */
	private int cooldown;
	
	/**
	 * 공격해야할 적군의 유닛ID
	 */
	private int targetUnitId;
	
	/**
	 * 공격해야할 적군의 유닛타입
	 */
	private String targetUnitType;
	
	/**
	 * 가까이 있는 터렛ID
	 */
	private int targetTurretUnitId;
	
	/**
	 * 가까이 있는 벙커ID
	 */
	private int targetBunkerUnitId;
	
	
	/**
	 * 공격해야할 적군의 유닛 수
	 */
	private int targetUnitsCnt;
	
	/**
	 * Q유닛을 공격할 수 있는 범위에 있는 적군의 수
	 */
	private int eneAttackCnt;
	
	/**
	 * Q유닛 시야에 있는 적군의 수
	 */
	private int eneCount;
	
	/**
	 * Q유닛 시야에 있는 터렛의 수
	 */
	private int eneTurretCnt;
	
	/**
	 * Q유닛 시야에 있는 적군리스트
	 */
	private List<Integer> eneList;
	
	/**
	 * Q유닛을 공격할수 있는 범위에 있는 적군리스트
	 */
	private List<Integer> eneAttackList;
	
	/**
	 * Q유닛과 적군유닛들의 최단거리
	 */
	private int minDistance;
	
	public List<QStateT.Grid> getGrdList() {
		return grdList;
	}

	public void setGrdList(List<Grid> grdList) {
		this.grdList = grdList;
	}

	public String getTargetUnitType() {
		return targetUnitType;
	}

	public void setTargetUnitType(String targetUnitType) {
		this.targetUnitType = targetUnitType;
	}

	public int getEneTurretCnt() {
		return eneTurretCnt;
	}

	public void setEneTurretCnt(int eneTurretCnt) {
		this.eneTurretCnt = eneTurretCnt;
	}

	public int getTargetTurretUnitId() {
		return targetTurretUnitId;
	}

	public void setTargetTurretUnitId(int targetTurretUnitId) {
		this.targetTurretUnitId = targetTurretUnitId;
	}

	public int getTargetBunkerUnitId() {
		return targetBunkerUnitId;
	}

	public int getMyHitPoints() {
		return myHitPoints;
	}

	public void setMyHitPoints(int myHitPoints) {
		this.myHitPoints = myHitPoints;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public int getTargetUnitsCnt() {
		return targetUnitsCnt;
	}

	public void setTargetUnitsCnt(int targetUnitsCnt) {
		this.targetUnitsCnt = targetUnitsCnt;
	}
	
	public int getEneAttackCnt() {
		return eneAttackCnt;
	}

	public void setEneAttackCnt(int eneAttackCnt) {
		this.eneAttackCnt = eneAttackCnt;
	}

	public int getEneCount() {
		return eneCount;
	}

	public void setEneCount(int eneCount) {
		this.eneCount = eneCount;
	}

	public int getTargetUnitId() {
		return targetUnitId;
	}

	public void setTargetUnitId(int unitId) {
		this.targetUnitId = unitId;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	public List<Integer> getEneList() {
		return eneList;
	}

	public void setEneList(List<Integer> eneList) {
		this.eneList = eneList;
	}
	
	public List<Integer> getEneAttackList() {
		return eneAttackList;
	}

	public void setEneAttackList(List<Integer> eneAttackList) {
		this.eneAttackList = eneAttackList;
	}

	class Grid implements Serializable {
		
		private static final long serialVersionUID = 5975653432126940401L;
		
		/**
		 * 그리드격자의 인덱스
		 */
		private int idx;
		
		/**
		 * 그리드 격자안의 적유닛 수
		 */
		private int eneCount;
		
		/**
		 * 그리드 격자안의 터렛 수
		 */
		private int eneTurretCnt;
		
		/**
		 * 이동해야할 X좌표
		 */
		private int movePositionX;
		
		/**
		 * 이동해야할 Y좌표
		 */
		private int movePositionY;
		
		public int getIdx() {
			return idx;
		}
		public void setIdx(int idx) {
			this.idx = idx;
		}
		public int getEneCount() {
			return eneCount;
		}
		public void setEneCount(int eneCount) {
			this.eneCount = eneCount;
		}
		public int getEneTurretCnt() {
			return eneTurretCnt;
		}
		public void setEneTurretCnt(int eneTurretCnt) {
			this.eneTurretCnt = eneTurretCnt;
		}
		public int getTargetUnit() {
			return targetUnitId;
		}
		public int getMovePositionX() {
			return movePositionX;
		}
		public void setMovePositionX(int movePositionX) {
			this.movePositionX = movePositionX;
		}
		public int getMovePositionY() {
			return movePositionY;
		}
		public void setMovePositionY(int movePositionY) {
			this.movePositionY = movePositionY;
		}
	}
	
	public QStateT(Game game) {
		
		Player self = game.self();
		Player enemy = game.enemy();
		Unit myUnit = QFlag.getMyUnit();
		List<Integer> eneList = new ArrayList<Integer>();
		List<Integer> eneAttackList = new ArrayList<Integer>();
		
		/*
		 * 아군 체력, cooldown의 합
		 */
		for (Unit selfUnit : self.getUnits()) {
			if (selfUnit.getType() == UnitType.Zerg_Mutalisk) {
				myHitPoints += selfUnit.getHitPoints();
				cooldown += selfUnit.getGroundWeaponCooldown();
			}
		}
		
		int unitDistance = Integer.MAX_VALUE;
		int tmpUnitDistance = Integer.MAX_VALUE;
		int tmpTurretUnitDistance = Integer.MAX_VALUE;
		
		eneAttackCnt = 0;
		eneCount = 0;
		eneTurretCnt = 0;
		for (Unit enemyUnit : enemy.getUnits()) {
			
			if (enemyUnit.getType() == UnitType.Terran_Marine 
					|| enemyUnit.getType() == UnitType.Terran_Bunker 
					|| enemyUnit.getType() == UnitType.Terran_Missile_Turret 
					|| enemyUnit.getType() == UnitType.Terran_Wraith
					|| enemyUnit.getType() == UnitType.Terran_Goliath
					) {
				// 시야에 보이지 않는 유닛은 스킵
				unitDistance = myUnit.getDistance(enemyUnit);
				if (unitDistance > QConstants.QMUTAL_SINGHT_RANGE) {
					continue;
				}
				eneCount++;
				eneList.add(enemyUnit.getID());

				// 내가 공격가능한 거리에 있는 경우
				int attackRange = QConstants.QMUTAL_GROUNDWEAPON_ATTCK_RANGE;
				if (attackRange >= unitDistance) {
					targetUnitsCnt++;
				}
				
				if (tmpUnitDistance > unitDistance) {
					tmpUnitDistance = unitDistance;
					this.setMinDistance(unitDistance);
					this.setTargetUnitId(enemyUnit.getID());
					this.setTargetUnitType(enemyUnit.getType()+"");
				}
				
				if (enemyUnit.getType() == UnitType.Terran_Missile_Turret || enemyUnit.getType() == UnitType.Terran_Goliath || enemyUnit.getType() == UnitType.Terran_Bunker) {
					eneTurretCnt++;
					// 상대방이 공격가능한 거리에 있는 경우
					if (UnitType.Terran_Missile_Turret.airWeapon().maxRange() + 10 > unitDistance) {
						eneAttackCnt++;
						eneAttackList.add(enemyUnit.getID());
					}
					
					if (tmpTurretUnitDistance > unitDistance) {
						tmpTurretUnitDistance = unitDistance;
						this.setTargetTurretUnitId(enemyUnit.getID());
					}
				}
				
				if (enemyUnit.getType() == UnitType.Terran_Marine|| enemyUnit.getType() == UnitType.Terran_Wraith) {

					// 상대방이 공격가능한 거리에 있는 경우
					if (UnitType.Terran_Marine.airWeapon().maxRange() + 20 > unitDistance) {
						eneAttackCnt++;
						eneAttackList.add(enemyUnit.getID());
					}
				}
			}
		}
		
		this.setEneList(eneList);
		this.setEneAttackList(eneAttackList);
		
		grdList = new ArrayList<QStateT.Grid>();
		int sightRange = QConstants.QMUTAL_SINGHT_RANGE; // 224
		int divideCnt = QConstants.SINGHT_DIVIDE_CNT;
		int cellSize = sightRange / divideCnt;
		int cellSizeHalf = cellSize / 2;
		int cellCenterInitX = myUnit.getPosition().getX() - cellSize*divideCnt;
		int cellCenterInitY = myUnit.getPosition().getY() - cellSize*divideCnt;
		
		int cellCenterXL = cellCenterInitX;
		int cellCenterYD = cellCenterInitY;
		int idx = 0;
		for (int i = 0; i < divideCnt*2; i++) {
			int cellCenterYU = cellCenterYD + cellSize;
			for (int j = 0; j < divideCnt*2; j++) {
				int cellCenterXR = cellCenterXL + cellSize;

				int x = (cellCenterXL + cellCenterXR) / 2;
				int y = (cellCenterYD + cellCenterYU) / 2;
				idx++;
				
				QStateT.Grid grd = new QStateT.Grid();
				grd.setIdx(idx);
				
				// 그리드 격자 안에 적군을 update한다.
				int eneGrdCount = 0;
				int eneGrdTurretCnt = 0;
				
				int fx = x - cellSizeHalf;
				int tx = x + cellSizeHalf;
				int fy = y - cellSizeHalf;
				int ty = y + cellSizeHalf;
				
				for (Unit eneUnit : enemy.getUnits()) {
					if (eneUnit.getType() == UnitType.Terran_Marine 
							|| eneUnit.getType() == UnitType.Terran_Bunker 
							|| eneUnit.getType() == UnitType.Terran_Missile_Turret 
							|| eneUnit.getType() == UnitType.Terran_Wraith
							|| eneUnit.getType() == UnitType.Terran_Goliath
							) {
						int eneX = eneUnit.getX();
						int eneY = eneUnit.getY();
						if (fx <= eneX && eneX <= tx) {
							if (fy <= eneY && eneY <= ty) {
								if (eneUnit.getType() == UnitType.Terran_Marine|| eneUnit.getType() == UnitType.Terran_Wraith) {
									eneGrdCount++;
								} else if (eneUnit.getType() == UnitType.Terran_Missile_Turret || eneUnit.getType() == UnitType.Terran_Goliath || eneUnit.getType() == UnitType.Terran_Bunker) {
									eneGrdTurretCnt++;
								}
							}
						}
					}
				}
				grd.setEneCount(eneGrdCount);
				grd.setEneTurretCnt(eneGrdTurretCnt);
				grd.setMovePositionX(x);
				grd.setMovePositionY(y);
				grdList.add(grd);
				cellCenterXL = cellCenterXL + cellSize;
				
			}
			cellCenterYD = cellCenterYD + cellSize;
			cellCenterXL = cellCenterInitX;
		}
	}

	/**
	 * 현재상태에서 진행가능한 Action을 리스트로 반환한다.
	 * @return
	 */
	public List<QAction> possibleActionsFromState() {
		List<QAction> actionList = new ArrayList<QAction>();
		// Move 액션
		for (Grid grid : this.getGrdList()) {
			QAction moveAction = new QAction();
			moveAction.setActionType(QConstants.ActionType.Move);
			moveAction.setMoveGrdIdx(grid.getIdx());
			// 갈수 없는 곳은 스킵처리
			if (QUtil.isUnreachablePosition(grid.getMovePositionX(), grid.getMovePositionY())) {
				continue;
			}
			moveAction.setMoveX(grid.getMovePositionX());
			moveAction.setMoveY(grid.getMovePositionY());
			moveAction.setTargetUnitType("moveAction");
			actionList.add(moveAction);
		}
		// 공격 액션
		if (this.getEneCount() > 0 && this.cooldown == 0) {
			QAction attackAction1 = new QAction();
			attackAction1.setActionType(QConstants.ActionType.Attack);
			attackAction1.setTargetId(this.getTargetUnitId());
			attackAction1.setTargetUnitType(this.getTargetUnitType());
			actionList.add(attackAction1);
		}

		return actionList;
	}

	/**
	 * Train을 해야하는 상태인지 여부를 리턴한다.
	 * @return
	 */
	public boolean isTrainingState() {
		
		// 나를 공격할 수 있는 적군이 있는 경우
		if (this.getEneAttackCnt() > 0) {
			return true;
		}
		
		// 공격할 수 있는 적이 있는 경우
		if (this.getTargetUnitsCnt() > 0) {
			return true;	
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (!(o instanceof QStateT)) {
			return false;
		}
		
		QStateT thisState = this;
		QStateT otherState = (QStateT) o;
		
		int thisEneCount = thisState.getEneCount();
		int otherEneCount = otherState.getEneCount();
		
		/*
		 * 공격대기시간 차이
		 * 공격직후와 공격이 가능한 상태에 차이를 둠
		 */
		int thisCooldown = thisState.getCooldown();
		int otherCooldown = otherState.getCooldown();
		if (otherCooldown == 0) {
			if (thisCooldown != 0) {
				return false;
			}
		}
		if (thisCooldown == 0) {
			if (otherCooldown != 0) {
				return false;
			}
		}
		
		/*
		 * 터렛의 숫자가 다르다면 다른상태
		 */
		if (thisEneCount < 8 && otherEneCount < 8) {
			if (thisState.getTargetUnitsCnt() != otherState.getTargetUnitsCnt()) {
				return false;
			}
		}

		/*
		 * 적이 공격할 수 있는 유닛의 갯수
		 */
		int thisEneAttackCnt = thisState.getEneAttackCnt();
		int otherEneAttackCnt = otherState.getEneAttackCnt();
		if (thisEneAttackCnt < 5 && otherEneAttackCnt < 5) {
			if (thisEneAttackCnt - thisEneAttackCnt != 0) {
				return false;
			}
		}
		
		/*
		 * 그리드 격자내의 유닛정보
		 */
		for (int i = 0; i < thisState.getGrdList().size(); i++) {
			Grid thisGid = thisState.getGrdList().get(i);
			Grid otherGid = otherState.getGrdList().get(i);
			
			/*
			 * 터렛의 숫자가 다르다면 다른상태
			 */
			if (thisEneCount < 8 && otherEneCount < 8) {
				if (thisGid.getEneTurretCnt() < 2 && otherGid.getEneTurretCnt() < 2) {
					if (thisGid.getEneTurretCnt() - otherGid.getEneTurretCnt() != 0) {
						return false;
					}
				}
			}
			
			/*
			 * 적군의 수
			 */
			int thisGrdEneCount = thisGid.getEneCount();
			int otherGrdEneCount = otherGid.getEneCount();
			
			if (thisGrdEneCount == 0) {
				if (otherGrdEneCount > 0) {
					return false;
				}
			}
			if (otherGrdEneCount == 0) {
				if (thisGrdEneCount > 0) {
					return false;
				}
			}
			
			if (thisGrdEneCount < 4 && otherGrdEneCount < 4) {
				if (thisGrdEneCount != otherGrdEneCount) {
					return false;
				}
			}
		}
        return true;
	}

	public int getMinAttackUnitGrdIdx(List<Grid> grdList) {
		int enemyCnt = Integer.MAX_VALUE;
		int retIdx = 0;
		for (Grid grid : grdList) {
			// 갈수 없는 곳은 스킵처리
			if (QUtil.isUnreachablePosition(grid.getMovePositionX(), grid.getMovePositionY())) {
				continue;
			}
			int tmpEnemyCnt = grid.getEneCount();
			if (enemyCnt > tmpEnemyCnt) {
				enemyCnt = tmpEnemyCnt;
				retIdx = grid.getIdx();
			}
		}
		return retIdx;
	}

	@Override
	public int getMinAttackUnitGrdIdx(Object grdList) {

		int enemyCnt = Integer.MAX_VALUE;
		int retIdx = 0;
		for (Grid grid : (List<Grid>) grdList) {
			// 갈수 없는 곳은 스킵처리
			if (QUtil.isUnreachablePosition(grid.getMovePositionX(), grid.getMovePositionY())) {
				continue;
			}
			int tmpEnemyCnt = grid.getEneCount();
			if (enemyCnt > tmpEnemyCnt) {
				enemyCnt = tmpEnemyCnt;
				retIdx = grid.getIdx();
			}
		}
		return retIdx;
		
		
		
		
		
		
	}

	/**
	 * 파라미터로 넘겨받은 그리드인덱스에 대해 해당되는 좌표를 셋팅하여 action으로 리턴한다.
	 */
	@Override
	public QAction getMoveActionWidGrdIdx(int grdIdx) {
		QAction moveAction = new QAction();
		moveAction.setActionType(QConstants.ActionType.Move);
		moveAction.setMoveGrdIdx(grdIdx);
		moveAction.setMoveX(this.getGrdList().get(grdIdx-1).getMovePositionX());
		moveAction.setMoveY(this.getGrdList().get(grdIdx-1).getMovePositionY());
		moveAction.setTargetUnitType("moveAction");
		return moveAction;
	}

	/**
	 * 접근가능한 좌표인지를 리턴
	 */
	@Override
	public boolean isUnreachableArea(int idx) {
		int x = this.getGrdList().get(idx-1).getMovePositionX();
		int y = this.getGrdList().get(idx-1).getMovePositionY();
		// 도달하지 못하는 부분 패스
		if (QUtil.isUnreachablePosition(x, y)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 현재 Action정보 반영
	 * maxQ로 가져온 이전 Action에 현재상태의 Action정보를 반영한다.
	 */
	@Override
	public QAction setAttackMaxQAction(QAction ret) {
		if (QConstants.ActionType.Attack.equals(ret.getActionType())) {
			
			if (ret.getTargetUnitType().equals(UnitType.Terran_Marine+"")) {
				ret.setTargetId(this.getTargetUnitId());
			} else if (ret.getTargetUnitType().equals(UnitType.Terran_Missile_Turret+"")) {
				ret.setTargetId(this.getTargetTurretUnitId());
			} else if (ret.getTargetUnitType().equals(UnitType.Terran_Bunker+"")) {
				ret.setTargetId(this.getTargetBunkerUnitId());
			}

		} else if (QConstants.ActionType.Move.equals(ret.getActionType())) {
			ret.setMoveX(this.getGrdList().get(ret.getMoveGrdIdx()-1).getMovePositionX());
			ret.setMoveY(this.getGrdList().get(ret.getMoveGrdIdx()-1).getMovePositionY());
		}
		return ret;
	}
}
