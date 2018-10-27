package AfternoonRacesRemote.Entities.Spectators.SpectatorsInterfaces;

/**
 * 
 * @author João Amaral
 * @author Francisco Cunha
 */
public interface ISpectator_Paddock {
    
    /**
     * 
     * @param spec_id
     */
    public void waitForNextRace(int spec_id);

    /**
     * 
     * @param spec_id
     */
    public void goCheckHorses(int spec_id);

}
