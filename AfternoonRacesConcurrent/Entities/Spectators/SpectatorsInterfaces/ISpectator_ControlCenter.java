package AfternoonRacesConcurrent.Entities.Spectators.SpectatorsInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface ISpectator_ControlCenter {
	
	// sync -> reportResults()
	public boolean haveIwon(int spec_id,  GeneralRepository general_repository);
		
	// sync -> vencedores pagos
	public void relaxABit(int spec_id);

	public void goWatchTheRace(int spec_id, GeneralRepository rep);
		
}
