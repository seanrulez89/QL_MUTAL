import bwapi.UnitType;

public class QConstants {
    
	/**
     * 학습여부
     * true : 랜덤으로 Action한다.
     * false : Max Q값으로 Action한다.
     */
    public static final boolean MLTraning = false;
    
	public static final int SINGHT_DIVIDE_CNT = 2;
	
	public static final int RANDOM_ACTION_CNT = 17;
	
	public static final int QMUTAL_GROUNDWEAPON_ATTCK_RANGE = UnitType.Zerg_Mutalisk.groundWeapon().maxRange() + 20;
	
	public static final int QMUTAL_SINGHT_RANGE = UnitType.Zerg_Mutalisk.sightRange() + 60; // 224 + 60
	
	public static final int MOVE_ACTION_DISTANCE = 70;
	
	public static final double ALPHA = 0.1;
	
	public static final double GAMMA = 0.9;

	public static final int Q_LEARNING_START_UNIT_CNT = 15;

	/**
	 * 스타크래프트 게임 Frame으로 인해 아래와 같이 Flag로 게임Frame을 제어한다.
	 * @author SDS
	 *
	 */
	class StateFlagType {
		/**
		 * Action을 시작한상태
		 */
	    static final String START = "START";
	    /**
	     * Action이 끝난상태
	     */
	    static final String END = "END";
	    /**
	     * Action을 시작 및 종료를 진행중인 상태
	     */
	    static final String ING = "ING";
	    /**
	     * 학습시작전 상태
	     */
	    static final String STANDBY = "STANDBY";
	}
	
	/**
	 * Action Type을 정의한 클래스
	 * @author SDS
	 *
	 */
	class ActionType {
	    static final String Attack = "Attack";
	    static final String Move = "Move";
	}
	
}