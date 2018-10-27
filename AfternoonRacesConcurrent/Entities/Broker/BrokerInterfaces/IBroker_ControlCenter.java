package AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface IBroker_ControlCenter {
	
	public void entertainTheGuests(GeneralRepository general_repository);

	public void reportResults(GeneralRepository rep);

	public boolean areThereAnyWinners(GeneralRepository general_repository);
	
}
