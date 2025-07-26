package com.example.samuraitravel.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "houses")		// [housesテーブル]をエンティティにマッピング
@Data						// Lambokによりゲッター、セッターなどを自動生成
// テーブル内の1行1行を[オブジェクト]として表現するための[クラス]
public class House {
// 各カラム名をエンティティのフィールドにマッピング
	// (カラム名：スネークケース / フィールド名：ローワーキャメルケース）
	@Id		// 主キーに指定するカラムにつける
	@GeneratedValue(strategy = GenerationType.IDENTITY)		// 自動採番の設定（テーブル内のAUTO_INCREMENTを指定したカラム(idカラム)を利用）
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "image_name")
	private String imageName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "capacity")
	private Integer capacity;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;	
}
// 管理者設定用の属性（default は [true]）
// insertable属性（挿入）：false = [管理者のみ]そのカラムに値を挿入できる (true = アプリ使用者が挿入操作可能の設定)
// updatable 属性（更新）：false = [管理者のみ]そのカラムの値を更新できる (true = アプリ使用者が挿入操作可能の設定)

// データベース側の設定（上記の属性とセットで設定）
// デフォルト値（CURRENT_TIMESTAMP）が自動的に挿入されるよう設定済
	// created_atカラム：DEFAULT CURRENT_TIMESTAMP
	// updated_atカラム：DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
