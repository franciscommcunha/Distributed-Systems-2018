package AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

/**
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface ISpectator_Paddock {
    
        /**
         * 
         * @param spec_id
         * @param rep 
         */
	public void waitForNextRace(int spec_id);
	
	/**
         * 
         * @param spec_id
         * @param rep 
         */
	public void goCheckHorses(int spec_id);

}
