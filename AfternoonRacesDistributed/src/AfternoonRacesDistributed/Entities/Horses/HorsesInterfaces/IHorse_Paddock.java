package AfternoonRacesDistributed.Entities.Horses.HorsesInterfaces;

import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

public interface IHorse_Paddock {
	
	public void proceedToStartLine(int horse_id);
	
	public void proceedToPaddock(int horse_id);

}
