package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.util.JDBCUtil;  
import model.dto.WishListDTO;

public class WishListDAO {
	private Connection conn; // DB와의 연결을 담당
	private PreparedStatement pstmt; // CRUD 수행을 담당

	private static final String SELECTALL_WISHLIST_RANK_BY_PRODUCT = 
			  "SELECT "
			+ "    RANK() OVER (ORDER BY COUNT(W.WISHLIST_ID) DESC) AS RANK, "
			+ "    P.PRODUCT_NAME, "
			+ "    COUNT(W.WISHLIST_ID) AS WISHLIST_COUNT "
			+ "FROM PRODUCT P "
			+ "JOIN WISHLIST W ON P.PRODUCT_ID = W.PRODUCT_ID "
			+ "GROUP BY P.PRODUCT_NAME "
			+ "ORDER BY RANK ";
	private static final String SELECTALL_WISHLIST_RANK_BY_GENDER = 
			"";
	private static final String SELECTALL_WISHLIST_RANK_BY_AGE = 
			"SELECT "
			+ "  CASE "
			+ "    WHEN AGE >= 10 AND AGE < 20 THEN '10대' "
			+ "    WHEN AGE >= 20 AND AGE < 30 THEN '20대' "
			+ "    WHEN AGE >= 30 AND AGE < 40 THEN '30대' "
			+ "    WHEN AGE >= 40 AND AGE < 50 THEN '40대' "
			+ "    ELSE '기타' "
			+ "  END AS 나이대, "
			+ "  PRODUCT_NAME, "
			+ "  COUNT(WISHLIST_ID) AS 총_상품_개수 "
			+ "FROM ( "
			+ "  SELECT "
			+ "    M.MEMBER_ID, "
			+ "    P.PRODUCT_BRAND, "
			+ "    P.PRODUCT_NAME, "
			+ "    P.PRODUCT_CATEGORY, "
			+ "    W.WISHLIST_ID, "
			+ "    TRUNC(MONTHS_BETWEEN(SYSDATE, M.MEMBER_BIRTH) / 12) AS AGE "
			+ "  FROM MEMBER M "
			+ "  JOIN WISHLIST W ON M.MEMBER_ID = W.MEMBER_ID "
			+ "  JOIN PRODUCT P ON W.PRODUCT_ID = P.PRODUCT_ID "
			+ ") Q "
			+ "WHERE AGE >= ? AND AGE < ? -- ??대인 멤버만 추출 "
			+ "GROUP BY CASE "
			+ "    WHEN AGE >= 10 AND AGE < 20 THEN '10대' "
			+ "    WHEN AGE >= 20 AND AGE < 30 THEN '20대' "
			+ "    WHEN AGE >= 30 AND AGE < 40 THEN '30대' "
			+ "    WHEN AGE >= 40 AND AGE < 50 THEN '40대' "
			+ "    ELSE '기타' "
			+ "  END, PRODUCT_NAME "
			+ "ORDER BY COUNT(WISHLIST_ID) DESC ";
	private static final String SELECTALL_WISHLIST_BY_MEMBER = 
			  "SELECT "
			+ "    M.MEMBER_NAME, "
			+ "    P.PRODUCT_BRAND, "
			+ "    P.PRODUCT_NAME, "
			+ "    P.PRODUCT_CATEGORY, "
			+ "    P.PRODUCT_PRICE, "
			+ "    P.PRODUCT_IMG "
			+ "FROM  "
			+ "    WISHLIST W "
			+ "JOIN "
			+ "    MEMBER M ON W.MEMBER_ID = M.MEMBER_ID "
			+ "JOIN "
			+ "    PRODUCT P ON W.PRODUCT_ID = P.PRODUCT_ID "
			+ "WHERE M.MEMBER_ID=? ";
	
	private static final String INSERT = "";
	/*
	  isWished 클래스를 가진 버튼이 클릭되면 
		 비동기적으로 상품을 추가하는 기능을 수행하고 싶어
		 isWished클래스를 가진 버튼이 클릭될때
		 해당 제품이 가진 isWished 변수의 값이 false라면 
		 데이터베이스에 있는 해당멤버의 위시리스트목록에 해당제품을 추가하고
		 true로 변경해준 뒤 하트에 빨강색을 칠해준다
		 만약 
		 회원이 iswished버튼을 클릭할 때
		 iswished의 값이 true라면
		 데이터베이스에 있는 해당멤버의 위시리스트목록에 있는 해당제품을 삭제하고
		 iswished의 값을 false로 변경해준뒤 색깔을 비워준다.
		 
		 jsp에서 비동기적으로 보여질 부분은 상품이미지 속 하트버튼
	 */
	private static final String DELETE = "";
	
	public ArrayList<WishListDTO> selectAll(WishListDTO wishListDTO){
		ArrayList<WishListDTO> datas = new ArrayList<WishListDTO>();
		if(wishListDTO.getSearchCondition().equals("회원별찜목록")) {
			conn=JDBCUtil.connect();
			try {
				PreparedStatement pstmt = conn.prepareStatement(SELECTALL_WISHLIST_BY_MEMBER);
				pstmt.setString(1, wishListDTO.getMemberID());
				System.out.println(wishListDTO.getMemberID()+"ㅁㄴㅇ");
				ResultSet rs = pstmt.executeQuery();
				
				while(rs.next()) {
					WishListDTO data = new WishListDTO();
					data.setProductBrand(rs.getString("PRODUCT_BRAND"));
					data.setProductName(rs.getString("PRODUCT_NAME"));
					System.out.println(rs.getString("PRODUCT_NAME"));
					data.setProductCategory(rs.getString("PRODUCT_CATEGORY"));
					data.setProductPrice(rs.getInt("PRODUCT_PRICE"));
					data.setProductImg(rs.getString("PRODUCT_IMG"));
					datas.add(data);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
//				JDBCUtil.disconnect(pstmt, conn);
			}
			return datas;
		}
		else if(wishListDTO.getSearchCondition().equals("제품별찜랭킹")) {
			conn=JDBCUtil.connect();
			try {
				PreparedStatement pstmt = conn.prepareStatement(SELECTALL_WISHLIST_RANK_BY_PRODUCT);
				ResultSet rs = pstmt.executeQuery();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.disconnect(pstmt, conn);
			}
			return datas;
		}
		else if(wishListDTO.getSearchCondition().equals("성별찜랭킹")) {
			conn=JDBCUtil.connect();
			try {
				PreparedStatement pstmt = conn.prepareStatement(SELECTALL_WISHLIST_RANK_BY_GENDER);
				ResultSet rs = pstmt.executeQuery();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.disconnect(pstmt, conn);
			}
			return datas;
		}
		else if(wishListDTO.getSearchCondition().equals("나이별찜랭킹")) {
			conn=JDBCUtil.connect();
			try {
				PreparedStatement pstmt = conn.prepareStatement(SELECTALL_WISHLIST_RANK_BY_AGE);
				ResultSet rs = pstmt.executeQuery();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.disconnect(pstmt, conn);
			}
			return datas;
		}
		else {
			return null;
		}
		
		
	}
		
	public boolean insert(WishListDTO wishListDTO) {	// 회원가입
		conn=JDBCUtil.connect();
		try {
			pstmt=conn.prepareStatement(INSERT);
			int result = pstmt.executeUpdate();
			if(result <= 0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.disconnect(pstmt, conn);
		}
		return true;
	}

	public boolean delete(WishListDTO wishListDTO) {
		conn=JDBCUtil.connect();
		try {
			pstmt=conn.prepareStatement(DELETE);
			int result = pstmt.executeUpdate();
			if(result<=0) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.disconnect(pstmt, conn);
		}
		return true;
	}
}
