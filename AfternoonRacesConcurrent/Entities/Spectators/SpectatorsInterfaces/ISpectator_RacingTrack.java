package AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface ISpectator_RacingTrack {
	
	// sync -> reportResults()
	public boolean haveIwon(int spec_id, GeneralRepository general_repository);
	
}
