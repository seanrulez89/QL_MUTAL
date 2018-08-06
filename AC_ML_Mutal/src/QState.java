import java.io.Serializable;
import java.util.List;

public abstract class QState implements Serializable {

	private static final long serialVersionUID = -8892712631736880336L;

	public abstract boolean isTrainingState();

	public abstract List<QAction> possibleActionsFromState();

	public abstract int getEneAttackCnt();

	public abstract List<Integer> getEneList();

	public abstract Object getGrdList();

	public abstract int getMinAttackUnitGrdIdx(Object grdList);

	public abstract int getTargetUnitId();

	public abstract int getCooldown();

	public abstract List<Integer> getEneAttackList();

	public abstract QAction getMoveActionWidGrdIdx(int grdIdx);

	public abstract boolean isUnreachableArea(int idx);

	public abstract QAction setAttackMaxQAction(QAction ret);

	public abstract int getMyHitPoints() ;
}
