package graphs.shortestpaths;

import graphs.BaseEdge;
import graphs.Graph;
import priorityqueues.DoubleMapMinPQ;
import priorityqueues.ExtrinsicMinPQ;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.List;

/**
 * Computes shortest paths using Dijkstra's algorithm.
 * @see ShortestPathFinder for more documentation.
 */
public class DijkstraShortestPathFinder<G extends Graph<V, E>, V, E extends BaseEdge<V, E>>
    implements ShortestPathFinder<G, V, E> {

    protected <T> ExtrinsicMinPQ<T> createMinPQ() {
        return new DoubleMapMinPQ<>();
        /*
        If you have confidence in your heap implementation, you can disable the line above
        and enable the one below.
        You'll also need to change the part of the class declaration that says
        `ArrayHeapMinPQ<T extends Comparable<T>>` to `ArrayHeapMinPQ<T>`.
         */
        // return new ArrayHeapMinPQ<>();

        /*
        Otherwise, do not change this method.
        We override this during grading to test your code using our correct implementation so that
        you don't lose extra points if your implementation is buggy.
         */
    }

    @Override
    public ShortestPath<V, E> findShortestPath(G graph, V start, V end) {
        if (Objects.equals(start, end)) {
            return new ShortestPath.SingleVertex<>(start);
        }

        //Create return object
        Map<V, E> predecessorEdges = new HashMap<>();

        //Mark all vertices as unprocessed
        Set<V> unprocessedVertices = createProcessingSet(graph, start, end);

        //Mark start as zero distance
        Map<V, Double> dist = new HashMap<>();
        dist.put(start, 0.);

        //Create a PQ that marks closest possible distances
        ExtrinsicMinPQ<V> pq = createMinPQ();

        //Update around start node
        update(graph, start, pq, dist, predecessorEdges);
        unprocessedVertices.remove(start);

        //Continue updating throughout the whole graph or until end node is processed
        while (!unprocessedVertices.isEmpty() && unprocessedVertices.contains(end)) {
            V curr = pq.removeMin();
            update(graph, curr, pq, dist, predecessorEdges);
            unprocessedVertices.remove(curr);
        }

        //Convert our predecessorEdge graph to a single path
        if (predecessorEdges.containsKey(end)) {
            List<E> path = getPath(predecessorEdges, start, end);
            return new ShortestPath.Success<>(path);
        }
        return new ShortestPath.Failure<>();
    }

    private Set<V> createProcessingSet(G graph, V start, V end) {
        Set<V> unprocessedVertices = new HashSet<>();
        Queue<V> perimeter = new LinkedList<>();
        Set<V> discovered = new HashSet<>();

        perimeter.add(start);
        discovered.add(start);

        while (!perimeter.isEmpty() && !discovered.contains(end)) {
            V curr = perimeter.remove();
            for (E edge : graph.outgoingEdgesFrom(curr)) {
                if (!discovered.contains(edge.to())) {
                    discovered.add(edge.to());
                    perimeter.add(edge.to());
                    unprocessedVertices.add(edge.to());
                }
            }
        }
        return unprocessedVertices;
    }

    private void update(G graph, V curr, ExtrinsicMinPQ<V> pq, Map<V, Double> dist, Map<V, E> retval) {
        for (E edge : graph.outgoingEdgesFrom(curr)) {
            V vertice = edge.to();
            double newDistance = edge.weight() + dist.get(curr);
            if (!dist.containsKey(vertice)) {
                dist.put(vertice, newDistance);
                pq.add(vertice, newDistance);
                retval.put(vertice, edge);
            } else if (newDistance < dist.get(vertice)) {
                dist.put(vertice, newDistance);
                pq.changePriority(vertice, newDistance);
                retval.put(vertice, edge);
            }
        }
    }

    private List<E> getPath(Map<V, E> predecessorEdges, V start, V end) {
        List<E> path = new LinkedList<>();
        V curr = end;
        while (!Objects.equals(start, curr)) {
            E link = predecessorEdges.get(curr);
            path.add(0, link);
            curr = link.from();
        }

        return path;
    }
}
