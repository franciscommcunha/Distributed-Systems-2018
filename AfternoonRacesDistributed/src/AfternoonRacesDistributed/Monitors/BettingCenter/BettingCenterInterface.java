package AfternoonRacesDistributed.Monitors.BettingCenter;

import AfternoonRacesDistributed.Messages.BettingCenterMessage.BettingCenterMessage;
import AfternoonRacesDistributed.Messages.BettingCenterMessage.BettingCenterMessageException;

/**
 * Interface for the BettingCenter monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class BettingCenterInterface {
    
    private BettingCenter bettingCenter;
    private boolean status = true;

    /**
     * 
     * @param stable BettingCenter reference
     */
    public BettingCenterInterface(BettingCenter bettingCenter) {
        this.bettingCenter = bettingCenter;
    }
    
    /**
     * Check if server alive or can end
     * @return boolean value to determine server lifecycle
     */
    public boolean getStatus() {
        return this.status;
    }
    
    /**
     * Function that executes the operation specified by each received message
     *
     * @param inputMsg Message containing the request
     * @return BettingCenterMessage reply
     * @throws BettingCenterMessageException if the message contains an invalid request
     */
    public BettingCenterMessage processRequest(BettingCenterMessage inputMsg) throws BettingCenterMessageException{

        BettingCenterMessage outputMsg = null;

        /*
         * Process the received message and generate response
         */
        switch (inputMsg.getMessageType()) {
            
            case BettingCenterMessage.ACCEPT_THE_BETS:
                bettingCenter.acceptTheBets();
                outputMsg = new BettingCenterMessage(BettingCenterMessage.OK);
                break;
                
            case BettingCenterMessage.PLACE_A_BET:
                bettingCenter.placeABet(inputMsg.getSpecId(), inputMsg.getHorseId(), inputMsg.getMoney());
                outputMsg = new BettingCenterMessage(BettingCenterMessage.OK);
                break;
             
            case BettingCenterMessage.HONOUR_THE_BETS:
                bettingCenter.honourTheBets();
                outputMsg = new BettingCenterMessage(BettingCenterMessage.OK);
                break;
                
            case BettingCenterMessage.GO_COLLECT_THE_GAINS:
                int money = (int) bettingCenter.goCollectTheGains(inputMsg.getSpecId());
                outputMsg = new BettingCenterMessage(BettingCenterMessage.GO_COLLECT_THE_GAINS, money);
                break;
                
            case BettingCenterMessage.END:
                System.out.println("END");
                this.status = false;
                outputMsg = new BettingCenterMessage(BettingCenterMessage.OK);
                break;
               
            default:
                throw new BettingCenterMessageException("Received invalid message type!", inputMsg);
        }

        return outputMsg;
    }
    
}
