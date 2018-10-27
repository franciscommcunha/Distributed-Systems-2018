package AfternoonRacesRemote.Entities.Spectators.SpectatorsInterfaces;

public interface ISpectator_ControlCenter {
	
    /**
     * 
     * @param spec_id
     * @return 
     */
    public boolean haveIwon(int spec_id);

    /**
     * 
     * @param spec_id 
     */
    public void relaxABit(int spec_id);

    /**
     * 
     * @param spec_id
     */
    public void goWatchTheRace(int spec_id);

}
