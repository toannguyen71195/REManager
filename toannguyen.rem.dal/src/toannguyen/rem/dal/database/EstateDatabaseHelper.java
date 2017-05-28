package toannguyen.rem.dal.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import toannguyen.rem.dal.mapping.AddressColumn;
import toannguyen.rem.dal.mapping.EstateColumn;
import toannguyen.rem.dal.mapping.EstateDetailColumn;
import toannguyen.rem.dal.mapping.EstateTypeColumn;
import toannguyen.rem.dal.mapping.InterestedEstateColumn;
import toannguyen.rem.dal.mapping.PhotoColumn;
import toannguyen.rem.entity.AddressEntity;
import toannguyen.rem.entity.Entity;
import toannguyen.rem.entity.EstateDetailEntity;
import toannguyen.rem.entity.EstateEntity;
import toannguyen.rem.entity.PhotoEntity;
import toannguyen.rem.entity.UserEntity;

public class EstateDatabaseHelper extends DatabaseHelper {

	public EstateDatabaseHelper() throws ClassNotFoundException, SQLException {
		super();
	}

	@Override
	protected Entity getEntityFromResultSet(ResultSet resultSet) throws Exception {
		UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper();
		try {
			UserEntity userEntity = userDatabaseHelper
					.getUserByID(resultSet.getInt(EstateColumn.OWNER_ID.getColumnName()));
			int id = resultSet.getInt(EstateColumn.ID.getColumnName());
			EstateDetailEntity detailEntity = queryEstateDetail(id);
			EstateEntity estateEntity = new EstateEntity(id, resultSet.getString(EstateColumn.NAME.getColumnName()),
					userEntity, getAddressEntityFromResultSet(resultSet),
					resultSet.getInt(EstateColumn.STATUS_ID.getColumnName()) == EstateEntity.STATUS_AVAILABLE,
					resultSet.getString(EstateTypeColumn.NAME.getColumnName()),
					resultSet.getTimestamp(EstateColumn.POST_TIME.getColumnName()),
					resultSet.getTimestamp(EstateColumn.EDIT_TIME.getColumnName()),
					resultSet.getDouble(EstateColumn.PRICE.getColumnName()),
					resultSet.getDouble(EstateColumn.AREA.getColumnName()));
			estateEntity.setDetail(detailEntity);
			return estateEntity;
		} finally {
			if (userDatabaseHelper != null) {
				userDatabaseHelper.closeConnection();
			}
		}
	}

	private AddressEntity getAddressEntityFromResultSet(ResultSet resultSet) throws SQLException {
		return new AddressEntity(resultSet.getInt(AddressColumn.ID.getColumnName()),
				resultSet.getString(AddressColumn.CITY.getColumnName()),
				resultSet.getString(AddressColumn.DISTRICT.getColumnName()),
				resultSet.getString(AddressColumn.WARD.getColumnName()),
				resultSet.getString(AddressColumn.STREET.getColumnName()),
				resultSet.getString(AddressColumn.ADDRESS.getColumnName()));
	}

	public List<EstateEntity> queryAllEstate() throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<EstateEntity> result = new ArrayList<>();
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM ");
			builder.append(EstateColumn.TABLE_NAME + " e left join ");
			builder.append(AddressColumn.TABLE_NAME + " a on e.");
			builder.append(EstateColumn.ADDRESS_ID + " = a.");
			builder.append(AddressColumn.ID);
			builder.append(" left join ");
			builder.append(EstateTypeColumn.TABLE_NAME + " t on e.");
			builder.append(EstateColumn.ESTATE_TYPE_ID + " = t.");
			builder.append(EstateTypeColumn.ID + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			while (rs.next()) {
				EstateEntity entity = (EstateEntity) getEntityFromResultSet(rs);
				result.add(entity);
			}
			return result;
		} finally {
			closeQuery(stmt, rs);
		}
	}

	public List<EstateEntity> queryEstateByOwnerID(int id) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<EstateEntity> entities = new ArrayList<>();
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM ");
			builder.append(EstateColumn.TABLE_NAME + " e left join ");
			builder.append(AddressColumn.TABLE_NAME + " a on e.");
			builder.append(EstateColumn.ADDRESS_ID + " = a.");
			builder.append(AddressColumn.ID);
			builder.append(" left join ");
			builder.append(EstateTypeColumn.TABLE_NAME + " t on e.");
			builder.append(EstateColumn.ESTATE_TYPE_ID + " = t.");
			builder.append(EstateTypeColumn.ID + " where e.");
			builder.append(EstateColumn.OWNER_ID + " = ");
			builder.append(id + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			while (rs.next()) {
				EstateEntity entity = (EstateEntity) getEntityFromResultSet(rs);
				entities.add(entity);
			}
			return entities;
		} finally {
			closeQuery(stmt, rs);
		}
	}

	public List<EstateEntity> queryNewEstate(int count) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<EstateEntity> result = new ArrayList<>();
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM ");
			builder.append(EstateColumn.TABLE_NAME + " e left join ");
			builder.append(AddressColumn.TABLE_NAME + " a on e.");
			builder.append(EstateColumn.ADDRESS_ID + " = a.");
			builder.append(AddressColumn.ID);
			builder.append(" left join ");
			builder.append(EstateTypeColumn.TABLE_NAME + " t on e.");
			builder.append(EstateColumn.ESTATE_TYPE_ID + " = t.");
			builder.append(EstateTypeColumn.ID + " where e.");
			builder.append(EstateColumn.STATUS_ID + " = " + EstateEntity.STATUS_AVAILABLE);
			builder.append(" order by " + EstateColumn.POST_TIME + " desc");
			builder.append(" limit " + count + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			while (rs.next()) {
				EstateEntity entity = (EstateEntity) getEntityFromResultSet(rs);
				result.add(entity);
			}
			return result;
		} finally {
			closeQuery(stmt, rs);
		}
	}

	public EstateDetailEntity queryEstateDetail(int estateId) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM ");
			builder.append(EstateDetailColumn.TABLE_NAME);
			builder.append(" where ");
			builder.append(EstateDetailColumn.ID + " = ");
			builder.append(estateId + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			rs.next();
			EstateDetailEntity entity = (EstateDetailEntity) getEstateDetailFromResultSet(rs);
			return entity;
		} finally {
			closeQuery(stmt, rs);
		}
	}

	private EstateDetailEntity getEstateDetailFromResultSet(ResultSet rs) throws Exception {
		try {
			EstateDetailEntity detailEntity = new EstateDetailEntity(rs.getInt(EstateDetailColumn.ID.getColumnName()),
					rs.getInt(EstateDetailColumn.BATHROOM.getColumnName()),
					rs.getInt(EstateDetailColumn.BEDROOM.getColumnName()),
					rs.getString(EstateDetailColumn.CONDITION.getColumnName()),
					rs.getString(EstateDetailColumn.DESCRIPTION.getColumnName()),
					rs.getInt(EstateDetailColumn.FLOOR.getColumnName()),
					rs.getDouble(EstateDetailColumn.LENGTH.getColumnName()),
					rs.getDouble(EstateDetailColumn.WIDTH.getColumnName()));
			return detailEntity;
		} catch (Exception e) {
			throw new Exception("rs detail " + e.getMessage());
		}

	}

	public List<EstateEntity> queryInterestedEstate(int userId) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<EstateEntity> entities = new ArrayList<>();
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM ");
			builder.append(InterestedEstateColumn.TABLE_NAME + " i left join ");
			builder.append(EstateColumn.TABLE_NAME + " e on i.");
			builder.append(InterestedEstateColumn.ESTATE_ID + " = e.");
			builder.append(EstateColumn.ID);
			builder.append(" left join ");
			builder.append(AddressColumn.TABLE_NAME + " a on e.");
			builder.append(EstateColumn.ADDRESS_ID + " = a.");
			builder.append(AddressColumn.ID);
			builder.append(" left join ");
			builder.append(EstateTypeColumn.TABLE_NAME + " t on e.");
			builder.append(EstateColumn.ESTATE_TYPE_ID + " = t.");
			builder.append(EstateTypeColumn.ID);
			builder.append(" where i.");
			builder.append(InterestedEstateColumn.USER_ID + " = ");
			builder.append(userId + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			while (rs.next()) {
				EstateEntity entity = (EstateEntity) getEntityFromResultSet(rs);
				entities.add(entity);
			}
			return entities;
		} finally {
			closeQuery(stmt, rs);
		}
	}

	public int getRate(int userId, int estateId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM ");
			builder.append(InterestedEstateColumn.TABLE_NAME);
			builder.append(" where ");
			builder.append(InterestedEstateColumn.USER_ID + " = ");
			builder.append(userId);
			builder.append(" and ");
			builder.append(InterestedEstateColumn.ESTATE_ID + " = ");
			builder.append(estateId + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(InterestedEstateColumn.RATE.getColumnName());
			} else {
				return 0;
			}
		} finally {
			closeQuery(stmt, rs);
		}
	}

	public void setInterested(int userId, int estateId, int rate) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			// check if interested => delete
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM ");
			builder.append(InterestedEstateColumn.TABLE_NAME);
			builder.append(" where ");
			builder.append(InterestedEstateColumn.USER_ID + " = ");
			builder.append(userId + " and ");
			builder.append(InterestedEstateColumn.ESTATE_ID + " = ");
			builder.append(estateId + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			if (rs.next()) {
				if (rate == 0) // delete
				{
					builder = new StringBuilder();
					builder.append("delete from " + InterestedEstateColumn.TABLE_NAME);
					builder.append(" where ");
					builder.append(InterestedEstateColumn.USER_ID + " = ");
					builder.append(userId + " and ");
					builder.append(InterestedEstateColumn.ESTATE_ID + " = ");
					builder.append(estateId + ";");
					executeUpdate(builder.toString());
				} else { // update
					builder = new StringBuilder();
					builder.append("update " + InterestedEstateColumn.TABLE_NAME);
					builder.append(" set " + InterestedEstateColumn.RATE + " = " + rate);
					builder.append(" where ");
					builder.append(InterestedEstateColumn.USER_ID + " = ");
					builder.append(userId + " and ");
					builder.append(InterestedEstateColumn.ESTATE_ID + " = ");
					builder.append(estateId + ";");
					executeUpdate(builder.toString());
				}
			} else { // insert
				builder = new StringBuilder();
				builder.append("insert into " + InterestedEstateColumn.TABLE_NAME);
				builder.append(" (" + InterestedEstateColumn.ESTATE_ID.getColumnName() + ", ");
				builder.append(InterestedEstateColumn.USER_ID + ", ");
				builder.append(InterestedEstateColumn.RATE + ") ");
				builder.append("values (" + estateId + ", " + userId + ", " + rate + ");");
				executeUpdate(builder.toString());
			}
		} finally {
			closeQuery(stmt, rs);
		}
	}

	public String getRepresentPhoto(int estateId) throws SQLException {
		// SELECT * FROM estate e left join photo p on e.PhotoID = p.PhotoID
		// where e.EstateID = 1;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM " + EstateColumn.TABLE_NAME);
			builder.append(" e left join " + PhotoColumn.TABLE_NAME + " p on e.");
			builder.append(EstateColumn.PHOTO_ID + " = p." + PhotoColumn.ID);
			builder.append(" where e." + EstateColumn.ID + " = " + estateId + ";");
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString(PhotoColumn.PHOTO.getColumnName());
			}
			return null;
		} finally {
			closeQuery(stmt, rs);
		}
	}

	public EstateEntity postEstate(EstateEntity reqEntity) throws Exception {
		// insert into address table
		AddressEntity addressEntity = null;
		AddressDatabaseHelper addressDatabaseHelper = null;
		try {
			addressDatabaseHelper = new AddressDatabaseHelper();
			addressEntity = addressDatabaseHelper.insertAddress(reqEntity.getAddress());
		} finally {
			if (addressDatabaseHelper != null) {
				addressDatabaseHelper.closeConnection();
			}
		}
		// insert into estate (AddressID, Name, OwnerID,
		// StatusID, EstateTypeID, PostTime, Price, Area, Photo)
		// values (1, 1, 'Ph�ng cho thu�', 3, 1, 2, '2017-05-06 05:06:07',
		// 12000, 80, 1);
		StringBuilder builder = new StringBuilder();
		builder.append("insert into " + EstateColumn.TABLE_NAME);
		builder.append(" (" + EstateColumn.ADDRESS_ID + ", ");
		builder.append(EstateColumn.NAME + ", ");
		builder.append(EstateColumn.OWNER_ID + ", ");
		builder.append(EstateColumn.STATUS_ID + ", ");
		builder.append(EstateColumn.ESTATE_TYPE_ID + ", ");
		builder.append(EstateColumn.POST_TIME + ", ");
		builder.append(EstateColumn.EDIT_TIME + ", ");
		builder.append(EstateColumn.PRICE + ", ");
		builder.append(EstateColumn.AREA + ") ");
		builder.append(" values (");
		builder.append("'" + addressEntity.getId() + "', ");
		builder.append("'" + reqEntity.getName() + "', ");
		builder.append(reqEntity.getOwner().getId() + ", ");
		builder.append("1, ");
		builder.append(reqEntity.getType() + ", ");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		builder.append("'" + timestamp.toString() + "', ");
		builder.append("'" + timestamp.toString() + "', ");
		builder.append(reqEntity.getPrice() + ", ");
		builder.append(reqEntity.getArea() + "');");
		executeUpdate(builder.toString());
		EstateEntity rtEstate = queryEstateByAddressID(addressEntity.getId());
		if (rtEstate == null) {
			throw new Exception("Server error after insert estate. Query return empty.");
		}
		// insert into estate detail
		EstateDetailEntity rtDetail = insertEstateDetail(reqEntity);
		if (rtDetail == null) {
			throw new Exception("Server error after insert detail. Query return empty.");
		}
		rtEstate.setDetail(rtDetail);
		return rtEstate;
	}

	private EstateEntity queryEstateByAddressID(int id) throws Exception {
		return (EstateEntity) super.queryByID(EstateColumn.TABLE_NAME, EstateColumn.ADDRESS_ID.getColumnName(), id);
	}

	private EstateDetailEntity insertEstateDetail(EstateEntity entity) throws Exception {
		// insert into estate_detail(Bathroom, Bedroom, Cond, Description,
		// Floor, Length, Width) values(1, 1, 'New', 'New', 2, 5, 5);
		EstateDetailEntity detail = entity.getDetail();
		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(EstateDetailColumn.TABLE_NAME);
		builder.append(" (" + EstateDetailColumn.ID + ",");
		builder.append(EstateDetailColumn.BATHROOM + ",");
		builder.append(EstateDetailColumn.BEDROOM + ",");
		builder.append(EstateDetailColumn.CONDITION + ",");
		builder.append(EstateDetailColumn.DESCRIPTION + ",");
		builder.append(EstateDetailColumn.FLOOR + ",");
		builder.append(EstateDetailColumn.LENGTH + ",");
		builder.append(EstateDetailColumn.WIDTH + ") values ('");
		builder.append(entity.getId() + "','");
		builder.append(detail.getBathroom() + "','");
		builder.append(detail.getBedroom() + "','");
		builder.append(detail.getCondition() + "','");
		builder.append(detail.getDescription() + "','");
		builder.append(detail.getFloor() + "','");
		builder.append(detail.getLength() + "','");
		builder.append(detail.getWidth() + "');");
		executeUpdate(builder.toString());
		return queryEstateDetail(entity.getId());
	}

	public EstateEntity editEstate(EstateEntity reqEntity) throws Exception {
		StringBuilder builder = new StringBuilder();
		try {
			// update address
			AddressEntity reqAddress = reqEntity.getAddress();
			builder.append("update " + AddressColumn.TABLE_NAME);
			builder.append(" set " + AddressColumn.CITY + " = '" + reqAddress.getCity() + "', ");
			builder.append(AddressColumn.DISTRICT + " = '" + reqAddress.getDistrict() + "', ");
			builder.append(AddressColumn.WARD + " = '" + reqAddress.getWard() + "', ");
			builder.append(AddressColumn.STREET + " = '" + reqAddress.getStreet() + "', ");
			builder.append(AddressColumn.ADDRESS + " = '" + reqAddress.getAddress());
			builder.append(" where " + AddressColumn.ID + " = " + reqAddress.getId() + ";");
			executeUpdate(builder.toString());
		} catch (Exception e) {
			throw new Exception("Server error after update address: " + e.getMessage());
		}
		// update estate
		try {
			builder.append("update " + EstateColumn.TABLE_NAME);
			builder.append(" set " + EstateColumn.NAME + " = '" + reqEntity.getName() + "', ");
			builder.append(EstateColumn.STATUS_ID + " = " + reqEntity.isAvailable() + ", ");
			builder.append(EstateColumn.AREA + " = " + reqEntity.getArea() + ", ");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			builder.append(EstateColumn.EDIT_TIME + " = '" + timestamp.toString() + "', ");
			builder.append(EstateColumn.PRICE + " = " + reqEntity.getPrice());
			builder.append(" where " + EstateColumn.ID + " = " + reqEntity.getId() + ";");
			executeUpdate(builder.toString());
		} catch (Exception e) {
			throw new Exception("Server error after update estate: " + e.getMessage());
		}
		// update detail
		try {
			EstateDetailEntity reqDetail = reqEntity.getDetail();
			builder.append("update " + EstateDetailColumn.TABLE_NAME);
			builder.append(" set " + EstateDetailColumn.BATHROOM + " = " + reqDetail.getBathroom() + ", ");
			builder.append(EstateDetailColumn.BEDROOM + " = " + reqDetail.getBedroom() + ", ");
			builder.append(EstateDetailColumn.CONDITION + " = '" + reqDetail.getCondition() + "', ");
			builder.append(EstateDetailColumn.DESCRIPTION + " = '" + reqDetail.getDescription() + "', ");
			builder.append(EstateDetailColumn.FLOOR + " = " + reqDetail.getFloor() + ", ");
			builder.append(EstateDetailColumn.LENGTH + " = " + reqDetail.getLength() + ", ");
			builder.append(EstateDetailColumn.WIDTH + " = " + reqDetail.getWidth());
			builder.append(" where " + EstateDetailColumn.ID + " = " + reqDetail.getId() + ";");
			executeUpdate(builder.toString());
		} catch (Exception e) {
			throw new Exception("Server error after update detail: " + e.getMessage());
		}
		return reqEntity;

	}

	public void upRepresentPhoto(PhotoEntity reqEntity) throws Exception {
		int upID = 0;
		StringBuilder builder = new StringBuilder();
		builder.append("insert into " + PhotoColumn.TABLE_NAME);
		builder.append(" (" + PhotoColumn.PHOTO + ", ");
		builder.append(PhotoColumn.TIME + "', ");
		builder.append(PhotoColumn.ESTATE_ID + ") ");
		builder.append("values ('" + reqEntity.getPhoto() + "','");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		builder.append(timestamp.toString() + "', ");
		builder.append(reqEntity.getEstateID() + ");");
		executeUpdate(builder.toString());

		builder = new StringBuilder();
		builder.append("select * from " + PhotoColumn.TABLE_NAME);
		builder.append(" where ");
		builder.append(PhotoColumn.TIME + " = '" + reqEntity.getTime().toString() + "' and ");
		builder.append(PhotoColumn.ESTATE_ID + " = " + reqEntity.getEstateID() + ";");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(builder.toString());
			rs = stmt.executeQuery();
			if (rs.next()) {
				upID = rs.getInt(PhotoColumn.ID.getColumnName());
			} else {
				throw new Exception("DB query, unable to retrieve photo back, please refresh");
			}
		} finally {
			closeQuery(stmt, rs);
		}

		builder = new StringBuilder();
		builder.append("update " + EstateColumn.TABLE_NAME);
		builder.append(" set " + EstateColumn.PHOTO_ID + " = " + upID);
		builder.append(" where " + EstateColumn.ID + " = " + reqEntity.getEstateID());
		executeUpdate(builder.toString());
	}

	public List<EstateEntity> queryTopRateEstate(int count) throws Exception {
		// SELECT *, SUM(i.Rate) as SumRate FROM interested_estate i
		// left join estate e on i.EstateID = e.EstateID
		// left join address a on a.AddressID = e.AddressID
		// group by i.EstateID
		// order by SumRate desc
		// limit 1;
		List<EstateEntity> entities = new ArrayList<>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT e.*, SUM(i.Rate) as SumRate FROM interested_estate i");
		builder.append(" left join estate e on i.EstateID = e.EstateID");
		builder.append(" left join address a on a.AddressID = e.AddressID");
		builder.append(" group by i.EstateID");
		builder.append(" order by SumRate desc");
		builder.append(" limit " + count + ";");
		stmt = con.prepareStatement(builder.toString());
		rs = stmt.executeQuery();
		while (rs.next()) {
			entities.add((EstateEntity) getEntityFromResultSet(rs));
		}
		return entities;
	}
}
