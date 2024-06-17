package scr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import scr.TrainingData;


/*
KD-Tree for an efficient implementation of the K-NN.
Do not touch!
*/
class KDTree {

    private KDNode root;
    private final int dim; // Number of dimensions

    public KDTree(List<TrainingData> points) {
        this.dim = 6;
        root = buildTree(points, this.dim);
    }

    private static class KDNode {
        TrainingData point;
        KDNode left, right;

        KDNode(TrainingData point) {
            this.point = point;
        }
    }

    private KDNode buildTree(List<TrainingData> points, int depth) {
        if (points.isEmpty()) {
            return null;
        }
        System.out.println(this.dim);
        int axis = depth % this.dim; 
        points.sort(Comparator.comparingDouble(p -> p.getData(axis)));
        int medianIndex = points.size() / 2;
        KDNode node = new KDNode(points.get(medianIndex));

        node.left = buildTree(points.subList(0, medianIndex), depth + 1);
        node.right = buildTree(points.subList(medianIndex + 1, points.size()), depth + 1);

        return node;
    }

    /*
     * Restituisce i k vicini più prossimi,
     * ordinando il risultato in base alla distanza così da restituire una lista
     * in cui è garantito che i vicini più prossimi siano effettivamente in ordine di distanza dal nodo corrente.
     */
    public List<TrainingData> kNearestNeighbors(TrainingData target, int k) {
        PriorityQueue<TrainingData> pq = new PriorityQueue<TrainingData>(k, Comparator.comparingDouble(target::distance).reversed());
        kNearestNeighbors(root, target, k, 0, pq);
        List<TrainingData> result = new ArrayList<TrainingData>(pq);
        result.sort(Comparator.comparingDouble(target::distance));
        return result;
    }

    private void kNearestNeighbors(KDNode node, TrainingData target, int k, int depth, PriorityQueue<TrainingData> pq) {
        if (node == null) {
            return;
        }

        double distance = target.distance(node.point);
        if (pq.size() < k) {
            pq.offer(node.point);
        } else if (distance < target.distance(pq.peek())) {
            pq.poll();
            pq.offer(node.point);
        }

        int axis = depth % this.dim;
        KDNode nearNode = (target.getData(axis) < node.point.getData(axis)) ? node.left : node.right;
        KDNode farNode = (nearNode == node.left) ? node.right : node.left;

        kNearestNeighbors(nearNode, target, k, depth + 1, pq);

        if (pq.size() < k || Math.abs((target.getData(axis) - node.point.getData(axis))) < target.distance(pq.peek())) {
            kNearestNeighbors(farNode, target, k, depth + 1, pq);
        }
    }
}