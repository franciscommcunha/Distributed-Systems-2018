package AfternoonRacesRemote.Registry;

/**
 *
 * @author Francisco Cunha
 * @author João Amaral
 */
public class RegistryConfig {
    
    public static final String RMI_REGISTER_NAME = "RegisterHandler";
    public static final String RMI_REGISTRY_HOSTNAME = "localhost";
    //public static final int RMI_REGISTRY_PORT = 22960; // may be not needed
    
    public static final int RMI_REGISTER_PORT = 22961;
    public static final int PORT_GENERAL_REPOSITORY = 22310;
    public static final int PORT_STABLE = 22311;
    public static final int PORT_PADDOCK = 22312;
    public static final int PORT_BETTING_CENTER= 22313;
    public static final int PORT_RACING_TRACK = 22314;
    public static final int PORT_CONTROL_CENTER = 22315;
    public static final int PORT_BROKER = 22316;
    public static final int PORT_HORSES = 22317;
    public static final int PORT_SPECTATORS = 22318;

    public static final String NAME_GENERAL_REPOSITORY = "l040101-ws01.ua.pt";
    public static final String RMI_REGISTRY_GENREPO_NAME = "GeneralRepository";
    
    public static final String NAME_STABLE = "l040101-ws02.ua.pt";
    public static final String RMI_REGISTRY_STABLE_NAME = "Stable";
    
    public static final String NAME_PADDOCK = "l040101-ws03.ua.pt";
    public static final String RMI_REGISTRY_PADDOCK_NAME = "Paddock";
    
    public static final String NAME_BETTING_CENTER = "l040101-ws04.ua.pt";
    public static final String RMI_REGISTRY_BETTINGCENTER_NAME = "BettingCenter";
    
    public static final String NAME_RACING_TRACK = "l040101-ws05.ua.pt";
    public static final String RMI_REGISTRY_RACINGTRACK_NAME = "RacingTrack";
    
    public static final String NAME_CONTROL_CENTER = "l040101-ws06.ua.pt";
    public static final String RMI_REGISTRY_CONTROLCENTER_NAME = "ControlCenter";
    
    public static final String NAME_REGISTRY = "l040101-ws07.ua.pt";
    
    public static final String NAME_BROKER = "l040101-ws08.ua.pt";
    public static final String RMI_REGISTRY_BROKER_NAME = "Broker";
    
    public static final String NAME_SPECTATORS = "l040101-ws09.ua.pt";
    public static final String RMI_REGISTRY_SPECTATORS_NAME = "Spectators";
    
    public static final String NAME_HORSES = "l040101-ws10.ua.pt";
    public static final String RMI_REGISTRY_HORSES_NAME = "Horses";
   
}