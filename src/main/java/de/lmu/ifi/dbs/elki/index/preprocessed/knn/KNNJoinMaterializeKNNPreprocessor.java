package de.lmu.ifi.dbs.elki.index.preprocessed.knn;

import de.lmu.ifi.dbs.elki.algorithm.KNNJoin;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.ids.distance.KNNList;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.rstar.RStarTreeNode;
import de.lmu.ifi.dbs.elki.logging.Logging;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2013
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Class to materialize the kNN using a spatial join on an R-tree.
 * 
 * @author Erich Schubert
 * 
 * @param <V> vector type
 * @param <D> distance type
 */
public class KNNJoinMaterializeKNNPreprocessor<V extends NumberVector<?>, D extends Distance<D>> extends AbstractMaterializeKNNPreprocessor<V, D, KNNList<D>> {
  /**
   * Logging class.
   */
  private static final Logging LOG = Logging.getLogger(KNNJoinMaterializeKNNPreprocessor.class);

  /**
   * Constructor.
   * 
   * @param relation Relation to index
   * @param distanceFunction Distance function
   * @param k k
   */
  public KNNJoinMaterializeKNNPreprocessor(Relation<V> relation, DistanceFunction<? super V, D> distanceFunction, int k) {
    super(relation, distanceFunction, k);
  }

  @Override
  protected void preprocess() {
    // Run KNNJoin
    KNNJoin<V, D, ?, ?> knnjoin = new KNNJoin<V, D, RStarTreeNode, SpatialEntry>(distanceFunction, k);
    storage = knnjoin.run(relation.getDatabase(), relation);
  }

  @Override
  protected Logging getLogger() {
    return LOG;
  }

  @Override
  public String getLongName() {
    return "knn-join materialized neighbors";
  }

  @Override
  public String getShortName() {
    return "knn-join";
  }

  @Override
  public void logStatistics() {
    // No statistics to log.
  }

  /**
   * The parameterizable factory.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.landmark
   * @apiviz.stereotype factory
   * @apiviz.uses AbstractMaterializeKNNPreprocessor oneway - - «create»
   * 
   * @param <O> The object type
   * @param <D> The distance type
   */
  public static class Factory<O extends NumberVector<?>, D extends Distance<D>> extends AbstractMaterializeKNNPreprocessor.Factory<O, D, KNNList<D>> {
    /**
     * Constructor.
     * 
     * @param k K
     * @param distanceFunction distance function
     */
    public Factory(int k, DistanceFunction<? super O, D> distanceFunction) {
      super(k, distanceFunction);
    }

    @Override
    public KNNJoinMaterializeKNNPreprocessor<O, D> instantiate(Relation<O> relation) {
      return new KNNJoinMaterializeKNNPreprocessor<>(relation, distanceFunction, k);
    }

    /**
     * Parameterization class
     * 
     * @author Erich Schubert
     * 
     * @apiviz.exclude
     * 
     * @param <O> Object type
     * @param <D> Distance type
     */
    public static class Parameterizer<O extends NumberVector<?>, D extends Distance<D>> extends AbstractMaterializeKNNPreprocessor.Factory.Parameterizer<O, D> {
      @Override
      protected KNNJoinMaterializeKNNPreprocessor.Factory<O, D> makeInstance() {
        return new KNNJoinMaterializeKNNPreprocessor.Factory<>(k, distanceFunction);
      }
    }
  }
}