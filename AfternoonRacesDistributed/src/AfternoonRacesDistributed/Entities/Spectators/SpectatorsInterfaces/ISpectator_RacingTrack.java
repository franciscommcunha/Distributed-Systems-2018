package AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

public interface ISpectator_RacingTrack {
	
	/**
         * 
         * @param spec_id
         * @param general_repository
         * @return 
         */
	public boolean haveIwon(int spec_id, GeneralRepository general_repository);
	
}
