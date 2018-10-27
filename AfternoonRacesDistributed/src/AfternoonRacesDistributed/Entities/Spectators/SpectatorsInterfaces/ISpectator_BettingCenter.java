package AfternoonRacesDistributed.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

public interface ISpectator_BettingCenter {
	
	/**
         * 
         * @param spec_id
         * @param horse_id
         * @param money
         * @param general_repository 
         */
	public void placeABet(int spec_id, int horse_id, int money);
	
	/**
         * 
         * @param spec_id
         * @param general_repository
         * @return 
         */
	public double goCollectTheGains(int spec_id);

}
