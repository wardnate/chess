package chess;

import java.util.ArrayList;
import java.util.Collection;

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
        int result = PieceType.hashCode();
        result = 31 * result + TeamColor.hashCode();
        return result;
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
        return moves;
    }
}