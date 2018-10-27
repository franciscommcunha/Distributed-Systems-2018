package AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface ISpectator_BettingCenter {
	
	// sync -> goCheckHorses()
	public void placeABet(int spec_id, int horse_id, int money, GeneralRepository general_repository);
	
	// sync -> reportResults() + if( haveIwon() )
	public double goCollectTheGains(int spec_id, GeneralRepository general_repository);

}
