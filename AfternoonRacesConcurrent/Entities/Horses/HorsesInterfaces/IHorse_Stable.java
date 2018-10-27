package AfternoonRacesConcurrent.Entities.Horses.HorsesInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface IHorse_Stable {
	
	public void proceedToStable(int horse_id, GeneralRepository rep);
	

}
