import java.util.Iterator;
import java.util.Map;

public class QThread implements Runnable {

	@Override
	public void run() {
		System.out.println("****** q thread start.");
		try {
			QUtil.readQTableFile(MyBotModule.Broodwar.enemy().getRace() + "");
			int qTableSize = QTable.getInstance().size();
			System.out.println("qTableSize : " + qTableSize);
			
			// 학습율 = 현재까지 진행된 액션건수 / 전체액션건수
			// 전체액션건수 : qTableSize * 17
//			Iterator<QStateT> qStatekeys = QTable.getInstance().keySet().iterator();
//			int trainingActionCnt = 0;
//	        while( qStatekeys.hasNext() ){
//	        	QStateT qState = qStatekeys.next();
//	        	Map<QAction, Double> qActionMap = QTable.getInstance().get(qState);
//	        	
//	    		Iterator<QAction> keys = qActionMap.keySet().iterator();
//	            while( keys.hasNext() ){
//	            	QAction qAction = keys.next();
//	            	if (qAction.getActionType().equals(QConstants.ActionType.Attack) || qAction.getActionType().equals(QConstants.ActionType.Move)) {
//	            		trainingActionCnt++;
//	    			}
//	            }
//	        }
//			double traningRate = (double) trainingActionCnt / ((double) qTableSize * 17);
//			System.out.println("****** q  thread traningRate : " + traningRate);
			QFlag.setFileReadComplete(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("****** q  thread end.");
	}
}
