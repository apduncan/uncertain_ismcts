import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class Space {
    private List<Cube> cubes;
    private Set<Color> colors;

    private static HashMap<List<Cube>, List<Cube>> sortMap = new HashMap<>();
    private static HashMap<List<Cube>, Set<Color>> colorMap = new HashMap<>();

    public Space() {
        this.cubes = new ArrayList<>();
        this.colors = new HashSet<>();
    }

    public Space(Space space) {
        // Copy constructor, does not copy adjacent tiles or spaces
        this();
        // If cloning a space, trust the cubes are in order
        this.cubes = new ArrayList<>(space.cubes);
        this.colors = new HashSet<>(space.colors);
    }

    public Space(List<Cube> cubes) {
        // Copy the list, do not use reference to list
        this();
        this.addCubes(cubes.stream().collect(Collectors.toList()));
    }

    public int getCubeCount() {
        return this.cubes.size();
    }

    public Set<Color> getColors() {
        return this.colors;
    }

    public void addCube(Cube cube) {
        this.addCubes(Arrays.asList(cube));
    }

    public void addCubes(Collection<Cube> cubes) {
        if(!Objects.isNull(cubes)) {
            this.cubes.addAll(cubes);
            // Sort list
            this.sortCubes();
        }
    }

    public void removeCube(Cube cube) {
        this.cubes.remove(cube);
        this.sortCubes();
    }

    private void sortCubes() {
//        if(sortMap.containsKey(this.cubes)) {
//            this.colors = new HashSet<>(colorMap.get(cubes));
//            this.cubes = new ArrayList<>(sortMap.get(cubes));
//        } else {
            List<Cube> sorted = this.cubes.stream().sorted(Cube::compareTo).collect(Collectors.toList());
            Set<Color> colors = this.getCubes().stream()
                    .map(Cube::getColor)
                    .collect(Collectors.toSet());
//            sortMap.put(this.cubes, sorted);
//            colorMap.put(this.cubes, colors);
            this.cubes = sorted;
            this.colors = colors;
//        }
    }

    public void clearCubes() {
        this.cubes = new ArrayList<>();
        this.colors = new HashSet<>();
    }

    public List<Cube> getCubes() {
        // Return a sorted list of cubes
        return this.cubes;
    }

    @Override
    public String toString() {
        String cubes = this.getCubes().stream()
                .map(c -> String.valueOf(c.getColor().toString().charAt(0)))
                .collect(Collectors.joining(""));
        return cubes;
    }

    public void setCubes(List<Cube> cubes) {
        this.cubes = cubes;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(this.getCubes())
                .toHashCode();
    }
}
