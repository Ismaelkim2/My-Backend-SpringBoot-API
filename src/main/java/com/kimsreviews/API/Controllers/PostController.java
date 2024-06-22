package com.kimsreviews.API.controllers;

import com.kimsreviews.API.Services.PostService;
import com.kimsreviews.API.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestParam("title") String title,
//                                           @RequestParam("content") String content,
                                           @RequestParam("productType") String productType,
                                           @RequestParam(value = "poultryType", required = false) String poultryType,
                                           @RequestParam(value = "weight", required = false) String weight,
                                           @RequestParam(value = "livestockType", required = false) String livestockType,
                                           @RequestParam(value = "livestockDescription", required = false) String livestockDescription,
                                           @RequestParam(value = "age", required = false) String age,
                                           @RequestParam(value = "salesAmount", required = false) String salesAmount,
                                           @RequestParam("image") MultipartFile image,
                                           @RequestParam("createdBy") String createdBy) {
        String imageUrl = saveImage(image);
        Post post = new Post();
        post.setTitle(title);
//        post.setContent(content);
        post.setProductType(productType);
        post.setPoultryType(poultryType);
        post.setWeight(weight);
        post.setLivestockType(livestockType);
        post.setLivestockDescription(livestockDescription);
        post.setAge(age);
        post.setSalesAmount(salesAmount);
        post.setImageUrl(imageUrl);
        post.setCreatedBy(createdBy);
        post.setCreatedAt(new java.util.Date());
        Post createdPost = postService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Post updatedPost = postService.updatePost(id, postDetails);
        if (updatedPost != null) {
            return ResponseEntity.ok(updatedPost);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Void> incrementLikes(@PathVariable Long id) {
        postService.incrementLikes(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        postService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    private String saveImage(MultipartFile image) {
        if (image.isEmpty()) {
            return null;
        }
        try {
            byte[] bytes = image.getBytes();
            Path path = Paths.get("uploads/" + image.getOriginalFilename());
            Files.write(path, bytes);
            return path.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
