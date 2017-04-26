package toannguyen.rem.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.google.gson.JsonElement;

import toannguyen.rem.dal.database.UserDatabaseHelper;
import toannguyen.rem.dal.user.UserDAL;
import toannguyen.rem.entity.Entity;
import toannguyen.rem.entity.json.EntityToJsonConverter;
import toannguyen.rem.server.resources.Strings;

@Path("/user")
public final class UserAPI {

	@GET
	@Path("/getAllUser")
	public String getAllUser() {
		try {
			List<Entity> users = UserDAL.getInstance().getAll(new UserDatabaseHelper());
			JsonElement element = EntityToJsonConverter.convertEntityListToJson(users);
			return element.toString();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			return Strings.error_internal + e.getMessage();
		}
	}
}
