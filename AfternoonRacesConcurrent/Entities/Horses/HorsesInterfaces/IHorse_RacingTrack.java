package AfternoonRacesConcurrent.Entities.Horses.HorsesInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface IHorse_RacingTrack {
	
	// sync -> 
	public void makeAMove(int horse_id, int agility, GeneralRepository rep);
		
	// sync -> 
	public boolean hasFinishLineBeenCrossed(int horse_id, GeneralRepository rep);

}
