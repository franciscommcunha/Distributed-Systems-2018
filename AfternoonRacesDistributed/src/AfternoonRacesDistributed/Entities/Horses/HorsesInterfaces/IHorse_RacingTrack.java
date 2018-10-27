package AfternoonRacesDistributed.Entities.Horses.HorsesInterfaces;

import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

public interface IHorse_RacingTrack {
	
	public void makeAMove(int horse_id, int agility);
		
	public boolean hasFinishLineBeenCrossed(int horse_id);

}
