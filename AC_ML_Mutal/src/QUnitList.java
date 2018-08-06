import java.util.ArrayList;

import bwapi.Position;
import bwapi.Unit;

/**
 * 아군 Q유닛들을 하나의 객체로 관리하기 위한 클래스
 * @author SDS
 *
 */
public class QUnitList extends ArrayList<Unit> {
	
	private static final long serialVersionUID = -2226241074238757331L;
	private CommandUtil commandUtil = new CommandUtil();
	
	public void attack(Unit targetUnit) {
		for (Unit unit : this) {
			unit.attack(targetUnit);
		}
	}
	
	public void attackMove(Unit targetUnit) {
		for (Unit unit : this) {
			commandUtil.attackUnit(unit, targetUnit);
		}
	}
	
	public void move(Position ps) {
		for (Unit unit : this) {
			unit.rightClick(ps);
		}
	}

	public void attack(Position ps) {
		for (Unit unit : this) {
			unit.attack(ps);
		}
	}
	
	public void attackMove(Position ps) {
		for (Unit unit : this) {
			commandUtil.attackMove(unit, ps);
		}
	}

	public void rightClick(Unit targetUnit) {
		for (Unit unit : this) {
			unit.rightClick(targetUnit);
		}
	}

	public void rightClick(Position ps) {
		for (Unit unit : this) {
			unit.rightClick(ps);
		}
	}
}
