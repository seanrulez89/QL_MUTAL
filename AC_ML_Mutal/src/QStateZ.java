import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class QStateZ extends QState implements Serializable {

	private static final long serialVersionUID = 8736090839819811430L;

	public QStateZ(Game broodwar) {
	}

	@Override
	public boolean isTrainingState() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<QAction> possibleActionsFromState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEneAttackCnt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Integer> getEneList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getGrdList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinAttackUnitGrdIdx(Object grdList) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTargetUnitId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCooldown() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Integer> getEneAttackList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QAction getMoveActionWidGrdIdx(int grdIdx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUnreachableArea(int idx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public QAction setAttackMaxQAction(QAction ret) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMyHitPoints() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
