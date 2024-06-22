package com.kimsreviews.API.Services;

import com.kimsreviews.API.models.Post;
import com.kimsreviews.API.Repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class PostService implements PostServic {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private PostRepo postRepo;

    @Override
    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        Optional<Post> optionalPost = postRepo.findById(id);
        return optionalPost.orElse(null);
    }

    @Override
    public Post createPost(Post post) {
        return postRepo.save(post);
    }

    @Override
    public Post updatePost(Long id, Post post) {
        Optional<Post> optionalExistingPost = postRepo.findById(id);
        if (optionalExistingPost.isPresent()) {
            Post existingPost = optionalExistingPost.get();

            // Update fields from the 'post' parameter
            existingPost.setTitle(post.getTitle());
//            existingPost.setContent(post.getContent());
            existingPost.setImageUrl(post.getImageUrl());
            existingPost.setCreatedBy(post.getCreatedBy());
            existingPost.setCreatedAt(post.getCreatedAt()); // Assuming createdAt is not updated

            // Update specific fields based on productType
            if ("Poultry".equals(post.getProductType())) {
                existingPost.setAge(post.getAge());
                existingPost.setPoultryType(post.getPoultryType());
                existingPost.setWeight(post.getWeight());
            } else if ("Livestock".equals(post.getProductType())) {
                existingPost.setAge(post.getAge());
                existingPost.setLivestockType(post.getLivestockType());
                existingPost.setLivestockDescription(post.getLivestockDescription());
            }

            existingPost.setSalesAmount(post.getSalesAmount());
            existingPost.setLikes(post.getLikes()); // Optionally update likes/views as needed
            existingPost.setViews(post.getViews());

            // Save the updated post
            return postRepo.save(existingPost);
        }
        return null; // or throw exception for not found
    }

    @Override
    public Post createPost(Post post, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            post.setImageUrl(imageUrl);
        }
        return postRepo.save(post);
    }

    @Override
    public Post updatePost(Long id, Post postDetails, MultipartFile image) throws IOException {
        Optional<Post> optionalExistingPost = postRepo.findById(id);
        if (optionalExistingPost.isPresent()) {
            Post existingPost = optionalExistingPost.get();
            existingPost.setTitle(postDetails.getTitle());
//            existingPost.setContent(postDetails.getContent());
            existingPost.setProductType(postDetails.getProductType());
            existingPost.setPoultryType(postDetails.getPoultryType());
            existingPost.setWeight(postDetails.getWeight());
            existingPost.setLivestockType(postDetails.getLivestockType());
            existingPost.setLivestockDescription(postDetails.getLivestockDescription());
            existingPost.setAge(postDetails.getAge());
            existingPost.setSalesAmount(postDetails.getSalesAmount());

            if (image != null && !image.isEmpty()) {
                String imageUrl = saveImage(image);
                existingPost.setImageUrl(imageUrl);
            }

            return postRepo.save(existingPost);
        }
        return null; // or throw exception for not found
    }

    @Override
    public void deletePost(Long id) {
        postRepo.deleteById(id);
    }

    @Override
    public void incrementLikes(Long id) {
        Optional<Post> optionalPost = postRepo.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setLikes(post.getLikes() + 1);
            postRepo.save(post);
        }
    }

    @Override
    public void incrementViews(Long id) {
        Optional<Post> optionalPost = postRepo.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setViews(post.getViews() + 1);
            postRepo.save(post);
        }
    }

    public String saveImage(MultipartFile image) throws IOException {
        String filename = image.getOriginalFilename();
        Path filepath = Paths.get(UPLOAD_DIR, filename);
        Files.createDirectories(filepath.getParent());
        Files.write(filepath, image.getBytes());
        return filepath.toString();
    }
}
