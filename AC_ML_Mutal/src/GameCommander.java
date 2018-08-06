import java.util.ArrayList;
import java.util.List;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;

// 실제 봇프로그램의 본체가 되는 class
// 스타크래프트 경기 도중 발생하는 이벤트들이 적절하게 처리되도록 해당 Manager 객체에게 이벤트를 전달하는 관리자 Controller 역할을 합니다
public class GameCommander {

	// [Q-Learing 추가] Qlearning 객체를 취득한다.
	private Qlearning ql;
	private boolean QlearningCombatIng = false;
	
    /// 디버깅용 플래그 : 어느 Manager 가 에러를 일으키는지 알기위한 플래그
    private boolean isToFindError = false;
    
	// [Q-Learing 추가] 공격포인트 지점리스트
	List<Position> targetPositionList = new ArrayList<Position>();
    
	public GameCommander() {
		
		// [Q-Learing 추가] Qlearning 객체를 취득한다.
		ql = Qlearning.getInstance();
	}
	
    // 경기가 시작될 때 일회적으로 발생하는 이벤트를 처리합니다
    public void onStart() {
		/*
		 * [Q-Learing 추가] Qlearning Traing 초기화
		 * - init(true) : 트레이닝모드 - 랜덤으로 Action한다.
		 * - init(false) : 전투모드 - MaxQ로 Action한다.
		 */
		QFlag.init();
		
        TilePosition startLocation = MyBotModule.Broodwar.self().getStartLocation();
        if (startLocation == TilePosition.None || startLocation == TilePosition.Unknown) {
            return;
        }
        ScoutManager.Instance().onStart();
        StrategyManager.Instance().onStart();
    }

    // 경기가 종료될 때 일회적으로 발생하는 이벤트를 처리합니다
    public void onEnd(boolean isWinner) {
    	
    	// [Q-Learing 추가] Qlearning Qtable 파일저장
    	if (QConstants.MLTraning) {
    		QUtil.saveFileToQTable(MyBotModule.Broodwar.enemy().getRace() + "");
		}

        StrategyManager.Instance().onEnd(isWinner);
    }

    // 경기 진행 중 매 프레임마다 발생하는 이벤트를 처리합니다
    public void onFrame() {
        if (MyBotModule.Broodwar.isPaused()
                || MyBotModule.Broodwar.self() == null || MyBotModule.Broodwar.self().isDefeated() || MyBotModule.Broodwar.self().leftGame()
                || MyBotModule.Broodwar.enemy() == null || MyBotModule.Broodwar.enemy().isDefeated() || MyBotModule.Broodwar.enemy().leftGame()) {
            return;
        }
        
        // [Q-Learing 추가] Qlearning training start
		/*
		 * Q Table 파일 Read - Runnable 객체를 매개변수로 하여 스레드 객체 th생성
		 * 이미 메모리에 있다면 읽지않는다
		 * (실제전투에서는 블럭킹될필요가 없으므로 쓰레드로 Q table을 읽는다)
		 */
		if (QFlag.isInitComplete() && MyBotModule.Broodwar.getFrameCount() == 100) {
			QThread runObj = new QThread();
	        Thread thd = new Thread(runObj);
	        thd.start();
		}
		
        if (MyBotModule.Broodwar.getGameType() == GameType.Use_Map_Settings) {
        	
        	// 일정 프레임이 지나면 재시작
        	if (MyBotModule.Broodwar.getFrameCount() == 24*60*90) {
        		System.out.println("Game End! from FrameCount");
				MyBotModule.Broodwar.restartGame();
			}
        	
        	// 뮤탈 숫자가 줄어들면 재시작
			if (QFlag.getMyQUnits().size() == 5) {
				System.out.println("Game End! from train");
				MyBotModule.Broodwar.restartGame();
			}
        	
        	if (QFlag.isFileReadComplete()) {
        		
        		// 적군 본진 타겟 위치
        		QFlag.setTargetPosition(new Position(803, 708));
        		
    			ql.train();
			}
        	
        	// [Q-Learing 추가] Qlearning training End
        	
		} else {
			
	       if (isToFindError) System.out.print("(a");

	        // 아군 베이스 위치. 적군 베이스 위치. 각 유닛들의 상태정보 등을 Map 자료구조에 저장/업데이트
	        InformationManager.Instance().update();

	        if (isToFindError) System.out.print("b");

	        // 각 유닛의 위치를 자체 MapGrid 자료구조에 저장
	        MapGrid.Instance().update();

	        if (isToFindError) System.out.print("c");

	        // economy and base managers
	        // 일꾼 유닛에 대한 명령 (자원 채취, 이동 정도) 지시 및 정리
	        WorkerManager.Instance().update();

	        if (isToFindError) System.out.print("d");

	        // 빌드오더큐를 관리하며, 빌드오더에 따라 실제 실행(유닛 훈련, 테크 업그레이드 등)을 지시한다.
	        BuildManager.Instance().update();

	        if (isToFindError) System.out.print("e");

	        // 빌드오더 중 건물 빌드에 대해서는, 일꾼유닛 선정, 위치선정, 건설 실시, 중단된 건물 빌드 재개를 지시한다
	        ConstructionManager.Instance().update();

	        if (isToFindError) System.out.print("f");

	        // 게임 초기 정찰 유닛 지정 및 정찰 유닛 컨트롤을 실행한다
	        ScoutManager.Instance().update();

	        if (isToFindError) System.out.print("g");

	        // 전략적 판단 및 유닛 컨트롤
	        StrategyManager.Instance().update();

	        if (isToFindError) System.out.print("h)");
	        
	        // [Q-Learing 추가] Qlearning training start
	        // Q table 파일 읽기 완료
		    if (QFlag.isFileReadComplete()) {
		    	
				/*
				 * 공격/도망 Position 생성
				 */
				QUtil.makeTargetPositionList(targetPositionList);
				
				// 출격할 준비가 되었는지 체크한다.
				if (QUtil.isCombatMode(targetPositionList)) {
					QlearningCombatIng = true;
				}
				if (QlearningCombatIng) {
					QFlag.setTargetPosition(targetPositionList.get(QFlag.getTargetPositionIdx()));
					ql.train();
				}
			}
		    
		 // [Q-Learing 추가] Qlearning training End
		}
    }

    // 유닛(건물/지상유닛/공중유닛)이 Create 될 때 발생하는 이벤트를 처리합니다
    public void onUnitCreate(Unit unit) {
        InformationManager.Instance().onUnitCreate(unit);
        
		/*
		 * [Q-Learing 추가] Qlearning 대상유닛 저장
		 */
		if (unit.getType() == UnitType.Zerg_Mutalisk) {
			// 유닛객체에 뮤탈을 추가한다. 
			QFlag.addMyQUnits(unit);
			// 대표유닛을 찾기위해 Que로 관리한다.
			QFlag.addMyQUnitsQue(unit);
		}
    }

    //  유닛(건물/지상유닛/공중유닛)이 Destroy 될 때 발생하는 이벤트를 처리합니다
    public void onUnitDestroy(Unit unit) {
		// [Q-Learing 추가] Qlearning 대상유닛 Destroy
		if (unit.getType() == UnitType.Zerg_Mutalisk) {
			QFlag.destroyMyQUnit(unit);
			QFlag.destroyMyQUnitQue(unit);
		}
		
        // ResourceDepot 및 Worker 에 대한 처리
        WorkerManager.Instance().onUnitDestroy(unit);

        InformationManager.Instance().onUnitDestroy(unit);
    }

    // 유닛(건물/지상유닛/공중유닛)이 Morph 될 때 발생하는 이벤트를 처리합니다
    // Zerg 종족의 유닛은 건물 건설이나 지상유닛/공중유닛 생산에서 거의 대부분 Morph 형태로 진행됩니다
    public void onUnitMorph(Unit unit) {
        InformationManager.Instance().onUnitMorph(unit);

        // Zerg 종족 Worker 의 Morph 에 대한 처리
        WorkerManager.Instance().onUnitMorph(unit);
        if (unit.getType().equals(UnitType.Zerg_Overlord)) {
            ScoutManager.Instance().assignScoutIfNeeded(unit);
        }
        
		/*
		 * [Q-Learing 추가] Qlearning 대상유닛 저장
		 */
		if (unit.getType() == UnitType.Zerg_Mutalisk) {
			// 유닛객체에 뮤탈을 추가한다. 
			QFlag.addMyQUnits(unit);
			// 대표유닛을 찾기위해 Que로 관리한다.
			QFlag.addMyQUnitsQue(unit);
		}
    }

    /// 유닛(건물/지상유닛/공중유닛)의 소속 플레이어가 바뀔 때 발생하는 이벤트를 처리합니다
    /// Gas Geyser에 어떤 플레이어가 Refinery 건물을 건설했을 때, Refinery 건물이 파괴되었을 때, Protoss 종족 Dark Archon 의 Mind Control 에 의해 소속 플레이어가 바뀔 때 발생합니다
    public void onUnitRenegade(Unit unit) {
        // Vespene_Geyser (가스 광산) 에 누군가가 건설을 했을 경우
        //MyBotModule.Broodwar.sendText("A %s [%p] has renegaded. It is now owned by %s", unit.getType().c_str(), unit, unit.getPlayer().getName().c_str());

        InformationManager.Instance().onUnitRenegade(unit);
    }

    // BasicBot 1.1 Patch Start ////////////////////////////////////////////////
    // 일꾼 탄생/파괴 등에 대한 업데이트 로직 버그 수정 : onUnitShow 가 아니라 onUnitComplete 에서 처리하도록 수정

    /// 유닛(건물/지상유닛/공중유닛)의 하던 일 (건물 건설, 업그레이드, 지상유닛 훈련 등)이 끝났을 때 발생하는 이벤트를 처리합니다
    public void onUnitComplete(Unit unit) {
        InformationManager.Instance().onUnitComplete(unit);

        // ResourceDepot 및 Worker 에 대한 처리
        WorkerManager.Instance().onUnitComplete(unit);
    }

    // BasicBot 1.1 Patch End //////////////////////////////////////////////////

    /// 유닛(건물/지상유닛/공중유닛)이 Discover 될 때 발생하는 이벤트를 처리합니다
    /// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
    public void onUnitDiscover(Unit unit) {
    }

    /// 유닛(건물/지상유닛/공중유닛)이 Evade 될 때 발생하는 이벤트를 처리합니다
    /// 유닛이 Destroy 될 때 발생합니다
    public void onUnitEvade(Unit unit) {
    }

    // BasicBot 1.1 Patch Start ////////////////////////////////////////////////
    // 일꾼 탄생/파괴 등에 대한 업데이트 로직 버그 수정 : onUnitShow 가 아니라 onUnitComplete 에서 처리하도록 수정

    /// 유닛(건물/지상유닛/공중유닛)이 Show 될 때 발생하는 이벤트를 처리합니다
    /// 아군 유닛이 Create 되었을 때 라든가, 적군 유닛이 Discover 되었을 때 발생합니다
    public void onUnitShow(Unit unit) {
        InformationManager.Instance().onUnitShow(unit);

        // ResourceDepot 및 Worker 에 대한 처리
        //WorkerManager.Instance().onUnitShow(unit);
    }

    // BasicBot 1.1 Patch End //////////////////////////////////////////////////

    /// 유닛(건물/지상유닛/공중유닛)이 Hide 될 때 발생하는 이벤트를 처리합니다
    /// 보이던 유닛이 Hide 될 때 발생합니다
    public void onUnitHide(Unit unit) {
        InformationManager.Instance().onUnitHide(unit);
    }

    // BasicBot 1.1 Patch Start ////////////////////////////////////////////////
    // onNukeDetect, onPlayerLeft, onSaveGame 이벤트를 처리할 수 있도록 메소드 추가

    /// 핵미사일 발사가 감지되었을 때 발생하는 이벤트를 처리합니다
    public void onNukeDetect(Position target) {
    }

    /// 다른 플레이어가 대결을 나갔을 때 발생하는 이벤트를 처리합니다
    public void onPlayerLeft(Player player) {
    }

    // 게임을 저장할 때 발생하는 이벤트를 처리합니다
    public void onSaveGame(String gameName) {
    }

    // BasicBot 1.1 Patch End //////////////////////////////////////////////////

    // 텍스트를 입력 후 엔터를 하여 다른 플레이어들에게 텍스트를 전달하려 할 때 발생하는 이벤트를 처리합니다
    public void onSendText(String text) {
    }

    // 다른 플레이어로부터 텍스트를 전달받았을 때 발생하는 이벤트를 처리합니다
    public void onReceiveText(Player player, String text) {
    }
}