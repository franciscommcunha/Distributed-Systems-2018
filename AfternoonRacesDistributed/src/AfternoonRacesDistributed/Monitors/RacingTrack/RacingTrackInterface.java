package AfternoonRacesDistributed.Monitors.RacingTrack;

import AfternoonRacesDistributed.Messages.RacingTrackMessage.RacingTrackMessage;
import AfternoonRacesDistributed.Messages.RacingTrackMessage.RacingTrackMessageException;

/**
 * Interface for the RacingTrack monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class RacingTrackInterface {
    
    private RacingTrack racingTrack;
    private boolean status = true;
    
    /**
     * @param racingTrack RacingTrack reference
     */
    public RacingTrackInterface(RacingTrack racingTrack) {
        this.racingTrack = racingTrack;
    }
    
    /**
     * Check if server alive or can end
     * @return boolean value to determine server lifecycle
     */
    public boolean getStatus() {
        return this.status;
    }
    
    /**
     * Method that executes the operation specified by each received message
     *
     * @param inputMsg Message containing the request
     * @return RacingTrackMessage reply
     * @throws RacingTrackMessageException if the message contains an invalid request
     */
    public RacingTrackMessage processRequest(RacingTrackMessage inputMsg) throws RacingTrackMessageException{

        RacingTrackMessage outputMsg = null;

        /*
         * Process the received message and generate response
         */
        switch (inputMsg.getMessageType()) {
            
            case RacingTrackMessage.START_THE_RACE:
                racingTrack.startTheRace();
                outputMsg = new RacingTrackMessage(RacingTrackMessage.OK);
                break;
                
            case RacingTrackMessage.MAKE_A_MOVE:
                racingTrack.makeAMove(inputMsg.getId(), inputMsg.getMoveUnit());
                outputMsg = new RacingTrackMessage(RacingTrackMessage.OK);
                break;
                
            case RacingTrackMessage.HAS_FINISHED_LINE_BEEN_CROSSED:
                boolean hasFinished;
                hasFinished= racingTrack.hasFinishLineBeenCrossed(inputMsg.getId());
                outputMsg = new RacingTrackMessage(RacingTrackMessage.HAS_FINISHED_LINE_BEEN_CROSSED, inputMsg.getId() ,hasFinished);
                break;
                
            case RacingTrackMessage.END:
                this.status = false;
                System.out.println("END");
                outputMsg = new RacingTrackMessage(RacingTrackMessage.END);
                break;
               
            default:
                throw new RacingTrackMessageException("Received invalid message type!", inputMsg);
        }
        return outputMsg;
    }
}