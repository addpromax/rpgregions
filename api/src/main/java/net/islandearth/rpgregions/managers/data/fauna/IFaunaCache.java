package net.islandearth.rpgregions.managers.data.fauna;

import net.islandearth.rpgregions.fauna.FaunaInstance;

import java.util.List;
import java.util.Optional;

public interface IFaunaCache {

    void reload();

    List<FaunaInstance<?>> getFauna();

    Optional<FaunaInstance<?>> getFauna(String id);

    void addFauna(FaunaInstance<?> instance);
}
