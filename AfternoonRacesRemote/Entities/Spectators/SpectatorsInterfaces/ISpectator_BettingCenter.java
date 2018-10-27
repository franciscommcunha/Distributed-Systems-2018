package AfternoonRacesRemote.Entities.Spectators.SpectatorsInterfaces;

public interface ISpectator_BettingCenter {
	
    /**
     * 
     * @param spec_id
     * @param horse_id
     * @param money
     */
    public void placeABet(int spec_id, int horse_id, int money);

    /**
     * 
     * @param spec_id
     * @return 
     */
    public double goCollectTheGains(int spec_id);

}
