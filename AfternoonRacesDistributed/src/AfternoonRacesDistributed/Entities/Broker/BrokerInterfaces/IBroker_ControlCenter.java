package AfternoonRacesDistributed.Entities.Broker.BrokerInterfaces;

import AfternoonRacesDistributed.Monitors.GeneralRepository.GeneralRepository;

public interface IBroker_ControlCenter {
	
	public void entertainTheGuests();

	public void reportResults();

	public boolean areThereAnyWinners();
	
}
