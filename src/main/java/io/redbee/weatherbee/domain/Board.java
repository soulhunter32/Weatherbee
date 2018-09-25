package io.redbee.weatherbee.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "BOARDS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Board implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "ID")
	private Integer id;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID")
	private User owner;
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "board")
//	@JoinTable(name="BOARDS_LOCATIONS", joinColumns={@JoinColumn(name="LOCATION_ID", referencedColumnName="ID")},
//		inverseJoinColumns={@JoinColumn(name="BOARD_ID", referencedColumnName="ID")})
	private List<Location> locations;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void addLocation(Location location) {
		location.setBoard(this);
		this.locations.add(location);
	}
	
	public void removeLocation(Location location) {
		this.locations.remove(location);
	}
	
	public void removeLocationById(Integer locationId) {
		locations.removeIf(location -> (locationId.equals(location.getId())));
	}
	
	public List<Location> getLocations() {
		return locations != null ? locations : (locations = new ArrayList<Location>());
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * Searches for a location in the location list whose city name is the one passed by parameter.-
	 * @param city
	 * @return the location whose city name is "city"
	 */
	public Location getLocationByCity(String city) {
		for (Location location : getLocations()) {
			if (location.getCity().equals(city)) {
				return location;
			}
		}
		return null;
	}
}
