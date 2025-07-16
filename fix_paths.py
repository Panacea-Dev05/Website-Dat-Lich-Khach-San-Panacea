import os
import re

def fix_html_paths(directory):
    """Sửa đường dẫn trong tất cả file HTML trong thư mục"""
    
    # Mapping từ đường dẫn cũ sang mới
    path_mapping = {
        'href="index.html"': 'href="/khachhang/"',
        'href="about-hotel-resort.html"': 'href="/khachhang/about"',
        'href="room.html"': 'href="/khachhang/rooms"',
        'href="single-room.html"': 'href="/khachhang/room-detail"',
        'href="resort.html"': 'href="/khachhang/resort"',
        'href="restaurent.html"': 'href="/khachhang/restaurant"',
        'href="spa-wellness.html"': 'href="/khachhang/spa-wellness"',
        'href="hotel-facilities.html"': 'href="/khachhang/facilities"',
        'href="event-list.html"': 'href="/khachhang/events"',
        'href="event-single-page.html"': 'href="/khachhang/event-detail"',
        'href="blog.html"': 'href="/khachhang/blog"',
        'href="blog-details.html"': 'href="/khachhang/blog-detail"',
        'href="faq.html"': 'href="/khachhang/faq"',
        'href="contact.html"': 'href="/khachhang/contact"',
        'href="404.html"': 'href="/khachhang/404"',
        'href="comming.html"': 'href="/khachhang/coming-soon"',
        'href="reservations.html"': 'href="/khachhang/booking"'
    }
    
    # Duyệt qua tất cả file HTML trong thư mục
    for filename in os.listdir(directory):
        if filename.endswith('.html'):
            filepath = os.path.join(directory, filename)
            print(f"Processing: {filename}")
            
            # Đọc file
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Sửa đường dẫn
            original_content = content
            for old_path, new_path in path_mapping.items():
                content = content.replace(old_path, new_path)
            
            # Ghi lại file nếu có thay đổi
            if content != original_content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  - Updated {filename}")
            else:
                print(f"  - No changes needed for {filename}")

if __name__ == "__main__":
    # Đường dẫn đến thư mục hotel-resort
    hotel_resort_dir = "src/main/resources/templates/KhachHang/livepreview/elegencia-main/hotel-resort"
    
    if os.path.exists(hotel_resort_dir):
        fix_html_paths(hotel_resort_dir)
        print("Path fixing completed!")
    else:
        print(f"Directory not found: {hotel_resort_dir}") 