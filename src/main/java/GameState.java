import java.util.*;

public enum GameState {
    // This enum functions as a finite state machine.
    // When calling the state's nextState method, pass previous and new game object, will determine which state
    // to move into
    MOVE_CUBES {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            // Move to CREATURE_UPDATE if all cubes moved
            int cubeCount = currentGame.getBoard().getActiveRowSpaces().stream()
                    .mapToInt(s -> s.getCubeCount())
                    .sum();
            if(cubeCount > 0) {
                return this.MOVE_CUBES;
            } else {
                return this.CREATURE_UPDATE;
            }
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return currentGame.getActivePlayerMoves();
        }
    },
    CREATURE_UPDATE {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            // Defensively ensure that the creature is no longer present on the active row
            int activePresent = currentGame.getBoard().getTilesWithCreature().size();
            if(activePresent > 0) {
                return this.CREATURE_UPDATE;
            }
            // Determine how many tiles the creature exists on in the inactive row.
            // If 0 or 1, move to a scientist win condition
            int inactivePresent = currentGame.getBoard().getInactiveRowTiles().stream().filter(Tile::isCreaturePresent)
                    .mapToInt(t -> 1).sum();
            if(inactivePresent < 2) {
                return this.SCIENTIST_WIN;
            }

            // Move to creature token claim
            return this.CREATURE_TOKENS;
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return currentGame.getCreatureUpdates();
        }
    },
    SCIENTIST_WIN {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            // Game is over, never change state
            return this.SCIENTIST_WIN;
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            // Terminal state - return only current gamestate
            return new HashSet<>(Arrays.asList(currentGame));
        }
    },
    CREATURE_WIN {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            return this.CREATURE_WIN;
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return new HashSet<>(Arrays.asList(currentGame));
        }
    },
    SCIENTIST_DROP {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            // If creature exists on both edges, move to creature win
            if(currentGame.getBoard().getInactiveRowTiles().get(0).isCreaturePresent() &&
                    currentGame.getBoard().getInactiveRowTiles().get(currentGame.getBoard().getInactiveRowTiles().size()-1).isCreaturePresent() &&
                    currentGame.getBoard().size() == currentGame.getBoard().getMaxWidth()
            ) {
                return this.CREATURE_WIN;
            }

            if(currentGame.getDroppedCubes().size() > 0) {
                return this.FREE_PLACE;
            }
            return this.DRAW_TILES;
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return currentGame.getScientistDrop();
        }
    },
    FREE_PLACE {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            return this.DRAW_TILES;
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return currentGame.getFreePlace();
        }
    },
    DRAW_TILES {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            return this.MOVE_CUBES;
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return currentGame.getDrawTiles();
        }
    },
    CREATURE_TOKENS {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            // If there aren't enough tiles to draw, move to tiebreaker condition
            if(currentGame.getTiles().size() < 2) {
                return this.TIEBREAKER;
            }
            // Move on to scientist drop, or draw tiles
            if(currentGame.getBoard().size() == currentGame.getBoard().getMaxWidth()) {
                return this.SCIENTIST_DROP;
            }
            return this.DRAW_TILES;
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return currentGame.getCreatureToken();
        }
    },
    TIEBREAKER {
        @Override
        public GameState nextState(Game prevGame, Game currentGame) {
            if(currentGame.getScientist().getTokens() > currentGame.getCreature().getTokens()) {
                return this.SCIENTIST_WIN;
            } else {
                return this.CREATURE_WIN;
            }
        }

        @Override
        public Set<Game> possibleGames(Game currentGame) {
            return currentGame.getTiebreaker();
        }
    };

    public abstract GameState nextState(Game prevGame, Game currentGame);
    public abstract Set<Game> possibleGames(Game currentGame);

    public static void main(String[] args) {
        Game game = new Game();
        // Play out a game at random
        while(!(game.getState() == GameState.SCIENTIST_WIN || game.getState() == GameState.CREATURE_WIN)) {
            System.out.println(game);
            List<Game> possible = new ArrayList<>(game.getState().possibleGames(game));
            Collections.shuffle(possible);
            game = possible.get(0);
            game.setState(game.getState().nextState(game, game));
        }
        System.out.println(game);
    }
}
