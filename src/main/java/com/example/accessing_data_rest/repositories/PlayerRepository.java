package com.example.accessing_data_rest.repositories;

import java.util.List;

import com.example.accessing_data_rest.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "players", path = "players")
public interface PlayerRepository extends PagingAndSortingRepository<Player, Long>, CrudRepository<Player, Long> {
    
    List<Player> findByUserUid(@Param("userId") Long userId); // for PlayerService
    List<Player> findByName(@Param("name") String name);

}
