package br.jus.tjce.releasecontroller.repository;

import br.jus.tjce.releasecontroller.model.BaseCalculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseCalculoRepository extends JpaRepository<BaseCalculo, Long> {
}
