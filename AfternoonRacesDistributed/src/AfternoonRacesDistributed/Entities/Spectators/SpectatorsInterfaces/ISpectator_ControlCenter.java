package AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

/**
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */

public interface ISpectator_ControlCenter {
	
	/**
         * 
         * @param spec_id
         * @param general_repository
         * @return 
         */
	public boolean haveIwon(int spec_id);
		
	/**
         * 
         * @param spec_id 
         */
	public void relaxABit(int spec_id);

        /**
         * 
         * @param spec_id
         * @param rep 
         */
	public void goWatchTheRace(int spec_id);
		
}
