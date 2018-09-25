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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "USERS")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name = "ID")
	private Integer id;

    @Column(name = "USERNAME")
	private String username;
	
    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = CascadeType.ALL)
//    @JoinTable(name="USERS_BOARDS", joinColumns={@JoinColumn(name="BOARD_ID", referencedColumnName="ID")},
//    	inverseJoinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")})
    private List<Board> boardList;
	
    public void addBoard(Board board) {
    	board.setOwner(this);
    	getBoardList().add(board);
    }
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<Board> getBoardList() {
		return boardList != null ? boardList : (boardList = new ArrayList<Board>());
	}
	public void setBoardList(List<Board> boardList) {
		this.boardList = boardList;
	}
}
