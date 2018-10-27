package AfternoonRacesDistributed.Monitors.GeneralRepository;

import AfternoonRacesDistributed.CommunicationChannel.CommunicationChannelServer;
import AfternoonRacesDistributed.Entities.Broker.BrokerEnum.BrokerStates;
import AfternoonRacesDistributed.Entities.Horses.HorsesEnum.HorseStates;
import AfternoonRacesDistributed.Entities.Spectators.SpectatorsEnum.SpectatorStates;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessage;
import AfternoonRacesDistributed.Messages.RepositoryMessage.RepositoryMessageException;
import java.util.Map;

/**
 * Interface for the general repository monitor
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public class GeneralRepositoryInterface {

    private GeneralRepository general_repository;
    
    private boolean status = true;
    
    /**
     * 
     * @param general_repository  repository reference
     */
    public GeneralRepositoryInterface(GeneralRepository general_repository) {
          this.general_repository = general_repository;
    }
    
    /**
     * Function that executes the operation specified by each received message
     *
     * @param input_msg Message containing the request
     * @return RepositoryMessage message response
     * @throws RepositoryMessageException if the message contains an invalid request
     */
    public RepositoryMessage processRequest(RepositoryMessage input_msg) {
        RepositoryMessage output_msg = null;

        /*
         * Process the received message and generate response
         */
        switch (input_msg.getMessageType()) {
            
            case RepositoryMessage.BROKER_STATE:
                BrokerStates broker_state = general_repository.getBrokerState();
                output_msg = new RepositoryMessage(RepositoryMessage.BROKER_STATE, broker_state);
                break;
                
            case RepositoryMessage.HORSE_STATE:
                HorseStates horse_state = general_repository.getHorseState(input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.HORSE_STATE, horse_state);
                break;
                
            case RepositoryMessage.SPECTATOR_STATE:
                SpectatorStates spectator_state = general_repository.getSpectatorState(input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.SPECTATOR_STATE, spectator_state);
                break;
                
            case RepositoryMessage.SET_BROKER_STATE:
                general_repository.setBrokerState(input_msg.getBrokerState());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
            
            case RepositoryMessage.SET_BET_INFO:
                general_repository.setBetInfo(input_msg.getId(), input_msg.getArray());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
            case RepositoryMessage.SET_HORSE_STATE:
                general_repository.setHorseState(input_msg.getHorseState(), input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
            case RepositoryMessage.SET_SPECTATOR_STATE:
                general_repository.setSpectatorState(input_msg.getSpectatorState(), input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
            
            case RepositoryMessage.ADD_HORSE_RESULT:
                general_repository.addHorseResult(input_msg.getArray());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
            case RepositoryMessage.REMOVE_HORSE_RESULT:
                int[] result = general_repository.removeHorseResult();
                output_msg = new RepositoryMessage(RepositoryMessage.OK, result);
                break;
                
            case RepositoryMessage.PEEK_HORSE_RESULT:
                int[] peek = general_repository.peekHorseResult();
                output_msg = new RepositoryMessage(RepositoryMessage.PEEK_HORSE_RESULT, peek);
                break;
                
            case RepositoryMessage.GET_HORSE_RESULT_SIZE:
                int size = general_repository.getHorseResultSize();
                output_msg = new RepositoryMessage(RepositoryMessage.GET_HORSE_RESULT_SIZE, size);
                break;
                
            case RepositoryMessage.ADD_HORSE_WINNER:
                general_repository.addHorseWinner(input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
            case RepositoryMessage.GET_BET_INFO_COPY:
                Map<Integer,int[]> map = general_repository.getBetInfoReferenceCopy();
                output_msg = new RepositoryMessage(RepositoryMessage.GET_BET_INFO_COPY, map);
                break;
                
            case RepositoryMessage.HORSE_IS_WINNER:
                boolean h_w = general_repository.horseIsWinner(input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.HORSE_IS_WINNER, h_w);
                break;
                
            case RepositoryMessage.ADD_WINNING_SPECTATOR:
                general_repository.addWinningSpectator(input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
             case RepositoryMessage.SPECTATOR_IS_WINNER:
                boolean s_w = general_repository.spectatorIsWinner(input_msg.getId());
                output_msg = new RepositoryMessage(RepositoryMessage.SPECTATOR_IS_WINNER, s_w);
                break;
               
             case RepositoryMessage.GET_WINNING_SPECTATOR_SIZE:
                 int win_spec_size = general_repository.getWinningSpectatorsSize();
                 output_msg = new RepositoryMessage(RepositoryMessage.GET_WINNING_SPECTATOR_SIZE,win_spec_size);
                 break;
                 
             case RepositoryMessage.CLEAR_BET_INFO: 
                general_repository.clearBetInfo();
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
            
             case RepositoryMessage.CLEAR_HORSE_WINNER:
                general_repository.clearHorseWinners();
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
            
             case RepositoryMessage.CLEAR_WINNING_SPECTATOR:  
                general_repository.clearWinningSpectators();
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
             case RepositoryMessage.INIT_LOG:
                 general_repository.initLog();
                 output_msg = new RepositoryMessage(RepositoryMessage.OK);
                 break;
                 
            case RepositoryMessage.WRITE_STAT_B:
                general_repository.writeStatB(input_msg.getBrokerState());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                 break;
                 
            case RepositoryMessage.WRITE_HORSE_ST:
                general_repository.writeHorseSt(input_msg.getHorseState());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                 break;
                 
            case RepositoryMessage.WRITE_SPEC_ST:
                general_repository.writeSpecSt(input_msg.getSpectatorState());
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
            case RepositoryMessage.MAKE_LOGGER:
                general_repository.makeLogger();
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
            case RepositoryMessage.CLOSE_WRITTER:
                general_repository.closeWriter();
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
                
            case RepositoryMessage.INC_RACE_ITERATION:
                general_repository.incRaceIteration();
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
             
            case RepositoryMessage.END:
                System.out.println("END");
                this.status = false;
                output_msg = new RepositoryMessage(RepositoryMessage.OK);
                break;
               
            default:
                throw new RepositoryMessageException("Received invalid message type!", input_msg);
        } 

        return output_msg;
    }
    
    /**
     * Check if server alive or can end
     * @return boolean value to determine server lifecycle
     */
    public boolean getStatus() {
        return this.status;
    }

}
