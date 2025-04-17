package com.base.demo.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.imageio.ImageIO;

import org.hibernate.engine.jdbc.BlobProxy;

import com.base.demo.dto.PhotoDto;
import com.base.demo.utils.ImageSize;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
@Table(name = "photo")
public class Photo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name = "resort_id")
  private Resort resort;
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name = "review_id")
  private Review review;

  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(nullable = false)
  private Blob data;

  @Column(nullable = false)
  private String ext;

  @Column(nullable = false)
  private Timestamp created;

  public Photo(Resort resort, Review review, String dataurl) {
    String[] parts = dataurl.split(",");
    this.ext = parts[0].split("/")[1].split(";")[0];
    this.data = BlobProxy.generateProxy(parts[1].getBytes());
    this.resort = resort;
    this.review = review;
    this.created = new Timestamp(System.currentTimeMillis());
  }

  public String getDataURL(ImageSize size) {
    return "data:image/" + ext + ";base64," + new String(resizeImage(size));
  }

  public String getDataURL() {
    return getDataURL(ImageSize.THUMBNAIL);
  }

  public byte[] resizeImage(ImageSize size) {
    try {
      BufferedImage originalImage = ImageIO.read(data.getBinaryStream());
      BufferedImage resizedImage = new BufferedImage(size.width(), size.height(), originalImage.getType());
      Graphics2D g = resizedImage.createGraphics();
      g.drawImage(originalImage, 0, 0, size.width(), size.height(), null);
      g.dispose();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(resizedImage, ext, baos);
      return baos.toByteArray();
    } catch (IOException | SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public byte[] resizeImage(){
    return resizeImage(ImageSize.THUMBNAIL);
  }

  public PhotoDto toPhotoDto(Integer index, ImageSize size) {
    return new PhotoDto(id, getDataURL(size), index);
  }

  public PhotoDto toPhotoDto(Integer index) {
    return new PhotoDto(id, getDataURL(ImageSize.FULL), index);
  }
}