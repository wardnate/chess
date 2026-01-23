package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessPiece.PieceType PieceType;
    private final ChessGame.TeamColor TeamColor;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return PieceType == that.PieceType && TeamColor == that.TeamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(PieceType, TeamColor);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.PieceType = type;
        this.TeamColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return TeamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return PieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece p1 = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();
        if (p1 == null) return moves;

        PieceType type = p1.getPieceType();

        if (type == PieceType.BISHOP) {
            int[][] directions = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
            };
            for (int[] direction : directions) {

                int dr = direction[0];
                int dc = direction[1];
                int r = myPosition.getRow() + dr;
                int c = myPosition.getColumn() + dc;

                while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else if (target.getTeamColor() != p1.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                        break;
                    } else {
                        break;
                    }
                    r += dr;
                    c += dc;
                }
            }
        }
        if (type == PieceType.ROOK) {
            int[][] directions = {
                    {0, 1},
                    {1, 0},
                    {-1, 0},
                    {0, -1}
            };
            for (int[] direction : directions) {

                int dr = direction[0];
                int dc = direction[1];
                int r = myPosition.getRow() + dr;
                int c = myPosition.getColumn() + dc;

                while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else if (target.getTeamColor() != p1.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                        break;
                    } else {
                        break;
                    }
                    r += dr;
                    c += dc;
                }
            }
        }
        if (type == PieceType.QUEEN) {
            int[][] directions = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1},
                    {0, 1},
                    {1, 0},
                    {-1, 0},
                    {0, -1}
            };
            for (int[] direction : directions) {

                int dr = direction[0];
                int dc = direction[1];
                int r = myPosition.getRow() + dr;
                int c = myPosition.getColumn() + dc;

                while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else if (target.getTeamColor() != p1.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                        break;
                    } else {
                        break;
                    }
                    r += dr;
                    c += dc;
                }
            }
        }
        if (type == PieceType.KING) {
            int[][] directions = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1},
                    {0, 1},
                    {1, 0},
                    {-1, 0},
                    {0, -1}
            };
            for (int[] direction : directions) {

                int dr = direction[0];
                int dc = direction[1];
                int r = myPosition.getRow() + dr;
                int c = myPosition.getColumn() + dc;

                if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else if (target.getTeamColor() != p1.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
            }
        }
        if (type == PieceType.KNIGHT) {
            int[][] directions = {
                    {2, 1},
                    {2, -1},
                    {-1, 2},
                    {1, 2},
                    {-2, 1},
                    {-2, -1},
                    {1, -2},
                    {-1, -2}
            };
            for (int[] direction : directions) {

                int dr = direction[0];
                int dc = direction[1];
                int r = myPosition.getRow() + dr;
                int c = myPosition.getColumn() + dc;

                if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {

                    ChessPosition newPos = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(newPos);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    } else if (target.getTeamColor() != p1.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
            }
        }
        if (type == PieceType.PAWN) {

            int r = myPosition.getRow();
            int c = myPosition.getColumn();

            if (p1.getTeamColor() == ChessGame.TeamColor.WHITE) {

                if (r+1 <= 8) {
                    // forward
                    ChessPosition forPos = new ChessPosition(r + 1, c);
                    ChessPiece forward = board.getPiece(forPos);
                    if (forward == null) {
                        if (r + 1 == 8) {
                            moves.add(new ChessMove(myPosition, forPos, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, forPos, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, forPos, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, forPos, PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(myPosition, forPos, null));
                        }
                    }
                }
                if (r==2) {
                    //start double forward
                    ChessPosition startPos = new ChessPosition(r + 2, c);
                    ChessPiece start = board.getPiece(startPos);

                    ChessPosition forPos = new ChessPosition(r + 1, c);
                    ChessPiece forward = board.getPiece(forPos);

                    if (start == null && forward == null) {
                        moves.add(new ChessMove(myPosition, startPos, null));
                    }
                }
                if (r + 1 <= 8 && c + 1 <= 8) {
                    //attack right
                    ChessPosition att1Pos = new ChessPosition(r + 1, c + 1);
                    ChessPiece att1 = board.getPiece(att1Pos);

                    if (att1 != null && att1.getTeamColor() != p1.getTeamColor()) {
                        if (r + 1 == 8) {
                            moves.add(new ChessMove(myPosition, att1Pos, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, att1Pos, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, att1Pos, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, att1Pos, PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(myPosition, att1Pos, null));
                        }
                    }
                }
                if (r + 1 <= 8 && c - 1 >= 1) {
                    // attack left
                    ChessPosition att2Pos = new ChessPosition(r + 1, c - 1);
                    ChessPiece att2 = board.getPiece(att2Pos);

                    if (att2 != null && att2.getTeamColor() != p1.getTeamColor()) {
                        if (r + 1 == 8) {
                            moves.add(new ChessMove(myPosition, att2Pos, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, att2Pos, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, att2Pos, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, att2Pos, PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(myPosition, att2Pos, null));
                        }
                    }
                }
            }
            if (p1.getTeamColor() == ChessGame.TeamColor.BLACK) {

                if (r - 1 >= 1) {
                    //forward
                    ChessPosition forPosB = new ChessPosition(r - 1, c);
                    ChessPiece forwardB = board.getPiece(forPosB);

                    if (forwardB == null) {
                        if (r - 1 == 1) {
                            moves.add(new ChessMove(myPosition, forPosB, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, forPosB, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, forPosB, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, forPosB, PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(myPosition, forPosB, null));
                        }
                    }
                }
                if (r == 7) {
                    //start double forward
                    ChessPosition startPosB = new ChessPosition(r - 2, c);
                    ChessPiece startB = board.getPiece(startPosB);
                    //forward
                    ChessPosition forPosB = new ChessPosition(r - 1, c);
                    ChessPiece forwardB = board.getPiece(forPosB);

                    if (startB == null && forwardB == null) {
                        moves.add(new ChessMove(myPosition, startPosB, null));
                    }
                }
                if (r - 1 >= 1 && c + 1 <= 8) {
                    //attack left
                    ChessPosition att1PosB = new ChessPosition(r - 1, c + 1);
                    ChessPiece att1B = board.getPiece(att1PosB);

                    if (att1B != null && att1B.getTeamColor() != p1.getTeamColor()) {
                        if (r - 1 == 1) {
                            moves.add(new ChessMove(myPosition, att1PosB, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, att1PosB, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, att1PosB, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, att1PosB, PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(myPosition, att1PosB, null));
                        }
                    }
                }
                if (r - 1 >= 1 && c - 1 >= 1) {
                    //attack right
                    ChessPosition att2PosB = new ChessPosition(r - 1, c - 1);
                    ChessPiece att2B = board.getPiece(att2PosB);

                    if (att2B != null && att2B.getTeamColor() != p1.getTeamColor()) {
                        if (r - 1 == 1) {
                            moves.add(new ChessMove(myPosition, att2PosB, PieceType.KNIGHT));
                            moves.add(new ChessMove(myPosition, att2PosB, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, att2PosB, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, att2PosB, PieceType.ROOK));
                        } else {
                            moves.add(new ChessMove(myPosition, att2PosB, null));
                        }
                    }
                }
            }

        }
        return moves;
    }
}

