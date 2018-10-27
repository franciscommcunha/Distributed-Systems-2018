package AfternoonRacesConcurrent.Entities.Horses.HorsesInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface IHorse_Paddock {
	
	// sync -> verficar se está no Paddock, esperar broker ou cavalo anterior
	public void proceedToStartLine(int horse_id, GeneralRepository rep);
	
	public void proceedToPaddock(int horse_id, GeneralRepository rep);

}
