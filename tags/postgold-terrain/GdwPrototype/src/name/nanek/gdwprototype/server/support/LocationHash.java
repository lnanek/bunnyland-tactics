package name.nanek.gdwprototype.server.support;

import name.nanek.gdwprototype.shared.model.Position;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Hash for a specific location on the game board.
 * 
 * @author Lance Nanek
 *
 */
public class LocationHash {

	private int row;

	private int column;

	public LocationHash(Position position) {
		this.row = position.getRow();
		this.column = position.getColumn();
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		LocationHash rhs = (LocationHash) obj;
		return new EqualsBuilder()
			.append(row, rhs.row)
			.append(column, rhs.column)
            .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(3, 7)
		.append(row)
		.append(column)
		.toHashCode();		
	}
}
