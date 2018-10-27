package AfternoonRacesConcurrent.Entities.Broker.BrokerInterfaces;

import AfternoonRacesConcurrent.Monitors.GeneralRepository;

public interface IBroker_BettingCenter {
	
	public void acceptTheBets(GeneralRepository general_repository);
	
	public void honourTheBets(GeneralRepository general_repository);

}
