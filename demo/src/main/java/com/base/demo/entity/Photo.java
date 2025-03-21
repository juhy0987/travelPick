package com.base.demo.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.base.demo.dto.PhotoDto;
import com.base.demo.utils.ImageSize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Photo {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "resort_id")
  private Resort resort;
  
  @ManyToOne
  @JoinColumn(name = "review_id")
  private Review review;

  @Column(nullable = false)
  private byte[] data;

  @Column(nullable = false)
  private Float score;

  @Column(nullable = false)
  private String ext;

  @Column(nullable = false)
  private Timestamp created;

  public Photo(Resort resort, Review review, String dataturl) {
    String[] parts = dataturl.split(",");
    this.ext = parts[0].split("/")[1].split(";")[0];
    this.data = java.util.Base64.getDecoder().decode(parts[1]);
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
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      BufferedImage originalImage = ImageIO.read(bais);
      BufferedImage resizedImage = new BufferedImage(size.width(), size.height(), originalImage.getType());
      Graphics2D g = resizedImage.createGraphics();
      g.drawImage(originalImage, 0, 0, size.width(), size.height(), null);
      g.dispose();
  
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(resizedImage, ext, baos);
      return baos.toByteArray();
    } catch (IOException e) {
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