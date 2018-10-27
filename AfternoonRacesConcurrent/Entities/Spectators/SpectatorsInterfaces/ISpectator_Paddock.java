package AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface ISpectator_Paddock {
	
	// sync -> ???
	public void waitForNextRace(int spec_id, GeneralRepository rep);
	
	// sync -> summonHorsesToPaddock()
	public void goCheckHorses(int spec_id, GeneralRepository rep);

}
