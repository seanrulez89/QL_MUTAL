import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import bwapi.Position;
import bwapi.Unit;

public class QFlag {
	
	/**
	 * 뮤탈유닛Que
	 * 뮤탈 9기중 대표로 1마리를 골라 대표유닛으로 지정한다.(que.peak)
	 * 대표유닛은 뮤탈 9기를 대표하여 다른 8기들의 시야, 위치등을 대표뮤탈로 갈음한다.
	 */
	private static Queue<Unit> myQUnitsQueue = new ArrayDeque<>();
	
	/**
	 * 유닛객체
	 */
	private static QUnitList myQUnits = new QUnitList();
	
	/**
	 * 현재상태
	 */
	private static QState crtState;
	
	/**
	 * 학습상태Flag
	 * STANDBY - 학습시작전 상태
	 * START - Action을 시작한상태
	 * END - Action이 끝난상태
	 * ING - Action을 시작 및 종료를 진행중인 상태
	 */
	private static String trainingStateFlag;
	
	/**
	 * 현재상태의 q값
	 * Action후 q값을 구할때 쓰인다
	 */
	private static double q;
	
	/**
	 * 선택된 다음Action
	 */
	private static QAction nextAction;
	
	/**
	 * 적군본진위치
	 */
	private static Position targetPosition;
	
	/**
	 * Move Action으로 이동하려는 Grid인덱스
	 */
	private static int movingTargetGrdIdx = 0;
	
	/**
	 * Attack Action으로 공격하려는 타겟유닛
	 */
	private static Unit targetUnit;
	
	/**
	 * 공격 타겟 인덱스
	 */
	private static int targetPositionIdx;
	
	/**
	 * 초기화 완료여부
	 */
	private static boolean isInitComplete = false;
	
	/**
	 * 해당플래그는 쓰레드가 Q Table을 모두 다 읽은 시점이다
	 */
	private static boolean isFileReadComplete = false;
	
	public static void setInitComplete(boolean isInitComplete) {
		QFlag.isInitComplete = isInitComplete;
	}

	public static boolean isInitComplete() {
		return isInitComplete;
	}
	public static int getTargetPositionIdx() {
		return targetPositionIdx;
	}

	public static void setTargetPositionIdx(int targetPositionIdx) {
		QFlag.targetPositionIdx = targetPositionIdx;
	}
	public static QState getCrtState() {
		return crtState;
	}

	public static void setCrtState(QState crtState2) {
		QFlag.crtState = crtState2;
	}

	public static String getTrainingStateFlag() {
		return trainingStateFlag;
	}

	public static void setTrainingStateFlag(String trainingStateFlag) {
		QFlag.trainingStateFlag = trainingStateFlag;
	}

	public static double getQ() {
		return q;
	}

	public static void setQ(double q) {
		QFlag.q = q;
	}

	public static QAction getNextAction() {
		return nextAction;
	}

	public static void setNextAction(QAction nextAction) {
		QFlag.nextAction = nextAction;
	}

	public static Position getTargetPosition() {
		return targetPosition;
	}

	public static void setTargetPosition(Position targetPosition) {
		QFlag.targetPosition = targetPosition;
	}

	public static Queue<Unit> getMyUnitQueue() {
		return myQUnitsQueue;
	}

	public static QUnitList getMyQUnits() {
		return myQUnits;
	}

	public static void addMyQUnits(Unit unit) {
		myQUnits.add(unit);
	}

	public static void destroyMyQUnit(Unit unit) {
		myQUnits.remove(unit);
	}

	public static void addMyQUnitsQue(Unit unit) {
		myQUnitsQueue.add(unit);
	}

	public static void destroyMyQUnitQue(Unit unit) {
		for (Unit qUnit : myQUnitsQueue) {
			//System.out.println("unit.getID() : " + unit.getID() + ", qUnit.getID() : " + qUnit.getID());
			if (unit.getID() == qUnit.getID()) {
				myQUnitsQueue.remove(qUnit);
			}
		}
	}
	
	public static Unit getMyUnit() {
//		System.out.println("=============");
//		for (Unit unit : myQUnitsQueue) {
//			System.out.println(unit.getID());
//		}
//		System.out.println("=============");
		return myQUnitsQueue.peek();
	}

	public static int getMovingTargetGrdIdx() {
		return movingTargetGrdIdx;
	}

	public static void setMovingTargetGrdIdx(int movingTargetGrdIdx) {
		QFlag.movingTargetGrdIdx = movingTargetGrdIdx;
	}

	public static Unit getTargetUnit() {
		return targetUnit;
	}

	public static void setTargetUnit(Unit targetUnit) {
		QFlag.targetUnit = targetUnit;
	}

	public static void init() {
		
		// 최초 q러닝 플래그 스탠바이
		QFlag.setTrainingStateFlag(QConstants.StateFlagType.STANDBY);
		
		// 트레이닝 상태 대기로 셋팅
		QFlag.setTrainingStateFlag(QConstants.StateFlagType.STANDBY);
		
		// 공격할 유닛
		QFlag.targetUnit = null;
		
		// 뮤탈유닛큐 - 대표가 되는 뮤탈을 관리하기 위함
		QFlag.myQUnitsQueue.clear();
		
		// 뮤탈유닛들객체
		myQUnits.clear();
		
		QFlag.setNextAction(null);
		
		// 파일 읽기 초기화
		QFlag.setFileReadComplete(false);
		
		// 공격지점 초기화
		QFlag.setTargetPositionIdx(0);
		
		// 초기화 완료
		QFlag.setInitComplete(true);
	}

	public static boolean isFileReadComplete() {
		return isFileReadComplete;
	}

	public static void setFileReadComplete(boolean isFileReadComplete) {
		QFlag.isFileReadComplete = isFileReadComplete;
	}
}