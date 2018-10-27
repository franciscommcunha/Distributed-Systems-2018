package AfternoonRacesRemote.Entities.Horses.HorsesInterfaces;

public interface IHorse_RacingTrack {
	
    public void makeAMove(int horse_id, int agility);
		
    public boolean hasFinishLineBeenCrossed(int horse_id);

}