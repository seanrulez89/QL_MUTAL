import java.io.Serializable;
import java.util.Objects;

/**
 * Action 객체
 * @author SDS
 *
 */
public class QAction implements Serializable {

	private static final long serialVersionUID = -8206823327625930748L;
	
	private String actionType;
	private int targetId;
	private String targetUnitType;
	private int moveGrdIdx;
	private int moveX;
	private int moveY;
	
	/**
	 * ActionType을 리턴한다
	 * Attack, Move
	 * @return actionType
	 */
	public String getActionType() {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
	/**
	 * 공격해야할 적유닛의 유닛ID를 리턴
	 * @return
	 */
	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	
	public String getTargetUnitType() {
		return targetUnitType;
	}
	
	public void setTargetUnitType(String targetUnitType) {
		this.targetUnitType = targetUnitType;
	}

	/**
	 * 이동해야할 그리드 격자의 인덱스를 리턴
	 * @return
	 */
	public int getMoveGrdIdx() {
		return moveGrdIdx;
	}

	public void setMoveGrdIdx(int moveGrdIdx) {
		this.moveGrdIdx = moveGrdIdx;
	}

	public int getMoveX() {
		return moveX;
	}

	public void setMoveX(int moveX) {
		this.moveX = moveX;
	}

	public int getMoveY() {
		return moveY;
	}

	public void setMoveY(int moveY) {
		this.moveY = moveY;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof QAction)) {
            return false;
        }
        QAction qActionObj = (QAction) obj;
        
        return this.moveGrdIdx == qActionObj.moveGrdIdx && Objects.equals(this.actionType, qActionObj.actionType) && Objects.equals(this.targetUnitType, qActionObj.targetUnitType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(actionType, targetUnitType, moveGrdIdx);
	}
}
