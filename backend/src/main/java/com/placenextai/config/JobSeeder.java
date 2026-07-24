package com.placenextai.config;

import com.placenextai.entity.Job;
import com.placenextai.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds real-world company job postings so the eligibility checker, skill-gap
 * roadmap and mock interview generator have a meaningful, recognizable company
 * list out of the box instead of only whatever a recruiter happens to post.
 * Idempotent - runs once, skips any title+company pair that already exists.
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class JobSeeder implements CommandLineRunner {

    private final JobRepository jobRepository;

    @Override
    public void run(String... args) {
        seed("Amazon", "Software Development Engineer I", "Bengaluru, India", "12-18 LPA",
                "Java,Data Structures,Algorithms,System Design,AWS,SQL", 7.0,
                "Design and build highly scalable, distributed backend systems as part of Amazon's engineering teams.");
        seed("Microsoft", "Software Engineer", "Hyderabad, India", "18-24 LPA",
                "C++,Data Structures,Algorithms,System Design,Azure,OOP", 7.5,
                "Build and ship features across Microsoft's cloud and productivity platforms.");
        seed("Google", "Software Engineer", "Bengaluru, India", "20-30 LPA",
                "Python,Algorithms,Data Structures,System Design,Machine Learning", 8.0,
                "Work on large-scale systems and products used by billions of people worldwide.");
        seed("Apple", "iOS Software Engineer", "Hyderabad, India", "18-26 LPA",
                "Swift,iOS,OOP,Data Structures,Algorithms", 7.5,
                "Design and build delightful, performant experiences for Apple's iOS platform.");
        seed("Meta", "Software Engineer", "Bengaluru, India", "22-32 LPA",
                "React,JavaScript,Python,System Design,Algorithms", 7.5,
                "Build products that connect billions of people across Meta's family of apps.");
        seed("Netflix", "Backend Engineer", "Remote", "25-35 LPA",
                "Java,Microservices,AWS,System Design,REST API", 7.5,
                "Build the streaming platform and content delivery systems powering Netflix globally.");
        seed("Adobe", "Member of Technical Staff", "Noida, India", "16-22 LPA",
                "Java,Data Structures,Algorithms,OOP,SQL", 7.0,
                "Develop features for Adobe's Creative Cloud and Experience Cloud products.");
        seed("Salesforce", "Software Engineer", "Hyderabad, India", "16-22 LPA",
                "Java,REST API,SQL,Cloud,System Design", 7.0,
                "Build scalable multi-tenant cloud services on the Salesforce platform.");
        seed("IBM", "Software Developer", "Bengaluru, India", "8-14 LPA",
                "Java,SQL,Cloud,DevOps,Linux", 6.5,
                "Develop enterprise software and hybrid cloud solutions for IBM's global clients.");
        seed("Oracle", "Applications Engineer", "Bengaluru, India", "10-16 LPA",
                "Java,SQL,DBMS,System Design,Linux", 7.0,
                "Build and maintain enterprise database and applications software at Oracle.");
        seed("SAP", "Associate Developer", "Bengaluru, India", "10-16 LPA",
                "Java,SQL,Cloud,REST API,OOP", 7.0,
                "Develop modules for SAP's enterprise resource planning cloud suite.");
        seed("Infosys", "Systems Engineer", "Pune, India", "4-7 LPA",
                "Java,SQL,Python,Testing", 6.0,
                "Deliver IT consulting and software solutions for Infosys's global clients.");
        seed("TCS", "Assistant System Engineer", "Chennai, India", "3.5-7 LPA",
                "Java,SQL,Python,DBMS", 6.0,
                "Build and support enterprise applications for TCS's global client base.");
        seed("Wipro", "Project Engineer", "Bengaluru, India", "3.5-6.5 LPA",
                "Java,SQL,Testing,OOP", 6.0,
                "Develop and test enterprise software solutions across Wipro's client engagements.");
        seed("Accenture", "Associate Software Engineer", "Pune, India", "4.5-8 LPA",
                "Java,SQL,Cloud,REST API", 6.5,
                "Build technology solutions for Accenture's consulting and outsourcing clients.");
        seed("Cognizant", "Programmer Analyst", "Chennai, India", "4-7 LPA",
                "Java,SQL,Python,Testing", 6.0,
                "Develop and maintain software applications for Cognizant's enterprise clients.");
        seed("Capgemini", "Software Engineer", "Mumbai, India", "4-7 LPA",
                "Java,SQL,Angular,REST API", 6.0,
                "Build full-stack applications for Capgemini's global delivery teams.");
        seed("HCLTech", "Software Engineer", "Noida, India", "4-7 LPA",
                "Java,SQL,Testing,Linux", 6.0,
                "Develop and support enterprise software for HCLTech's global clients.");
        seed("Tech Mahindra", "Software Engineer", "Pune, India", "3.5-6.5 LPA",
                "Java,SQL,Networking,OOP", 6.0,
                "Build telecom and enterprise software solutions at Tech Mahindra.");
        seed("Deloitte", "Technology Analyst", "Gurugram, India", "6-10 LPA",
                "SQL,Data Analysis,Excel,Python", 6.5,
                "Deliver technology and analytics consulting for Deloitte's enterprise clients.");
        seed("Goldman Sachs", "Technology Analyst", "Bengaluru, India", "18-26 LPA",
                "Java,Python,SQL,Data Structures,Algorithms", 8.0,
                "Build trading and risk platforms for Goldman Sachs's global markets business.");
        seed("JPMorgan Chase", "Software Engineer", "Mumbai, India", "16-24 LPA",
                "Java,Python,SQL,System Design", 7.5,
                "Develop banking and financial technology platforms at global scale.");
        seed("Morgan Stanley", "Technology Analyst", "Bengaluru, India", "16-24 LPA",
                "Java,Python,Data Structures,SQL", 7.5,
                "Build systems supporting Morgan Stanley's global investment banking operations.");
        seed("Flipkart", "SDE-1", "Bengaluru, India", "14-22 LPA",
                "Java,Data Structures,Algorithms,System Design,SQL", 7.0,
                "Build e-commerce platform features serving millions of customers across India.");
        seed("Zomato", "Backend Developer", "Gurugram, India", "10-16 LPA",
                "Node.js,JavaScript,MongoDB,REST API", 6.5,
                "Build backend services powering Zomato's food delivery and discovery platform.");
        seed("Swiggy", "Software Engineer", "Bengaluru, India", "12-18 LPA",
                "Java,Microservices,SQL,System Design", 7.0,
                "Build scalable logistics and ordering systems for Swiggy's delivery platform.");
        seed("Paytm", "Software Engineer", "Noida, India", "10-16 LPA",
                "Java,Spring Boot,SQL,REST API", 6.5,
                "Build secure, scalable payment and fintech services at Paytm.");
        seed("PhonePe", "SDE-1", "Bengaluru, India", "14-20 LPA",
                "Java,Spring Boot,Microservices,SQL", 7.0,
                "Build high-throughput payment infrastructure serving millions of transactions.");
        seed("Uber", "Software Engineer", "Bengaluru, India", "18-26 LPA",
                "Python,System Design,Algorithms,SQL", 7.5,
                "Build the systems powering Uber's global mobility and delivery platform.");
        seed("Ola", "Software Engineer", "Bengaluru, India", "10-16 LPA",
                "Java,Python,SQL,Microservices", 6.5,
                "Build backend systems for Ola's ride-hailing and mobility platform.");
        seed("Intel", "Software Engineer", "Bengaluru, India", "12-18 LPA",
                "C++,C,OOP,Data Structures", 7.0,
                "Develop system software and tools for Intel's hardware and silicon platforms.");
        seed("NVIDIA", "Software Engineer", "Bengaluru, India", "18-26 LPA",
                "C++,Python,Machine Learning,Deep Learning", 7.5,
                "Build software for NVIDIA's GPU-accelerated computing and AI platforms.");
        seed("Qualcomm", "Embedded Software Engineer", "Hyderabad, India", "14-20 LPA",
                "C,C++,Linux,OOP", 7.0,
                "Develop embedded and systems software for Qualcomm's chipset platforms.");
        seed("Cisco", "Software Engineer", "Bengaluru, India", "14-20 LPA",
                "C++,Networking,Linux,Python", 7.0,
                "Build networking software and infrastructure at Cisco's engineering centers.");
        seed("VMware", "Member of Technical Staff", "Bengaluru, India", "16-22 LPA",
                "C++,Java,Linux,System Design", 7.0,
                "Develop virtualization and cloud infrastructure software at VMware.");
        seed("ServiceNow", "Software Engineer", "Hyderabad, India", "16-22 LPA",
                "JavaScript,Java,REST API,SQL", 7.0,
                "Build workflow automation software on the ServiceNow platform.");
        seed("Atlassian", "Software Engineer", "Bengaluru, India", "18-26 LPA",
                "Java,React,REST API,System Design", 7.0,
                "Build collaboration and developer tools used by teams worldwide.");
        seed("LinkedIn", "Software Engineer", "Bengaluru, India", "20-28 LPA",
                "Java,System Design,SQL,Microservices", 7.5,
                "Build the platform connecting professionals across LinkedIn's global network.");
        seed("Spotify", "Backend Engineer", "Remote", "18-26 LPA",
                "Python,Java,Microservices,REST API", 7.0,
                "Build backend services powering music streaming and recommendations at scale.");
        seed("Airbnb", "Software Engineer", "Remote", "22-30 LPA",
                "React,JavaScript,Python,System Design", 7.5,
                "Build the platform connecting hosts and guests across Airbnb's global marketplace.");
    }

    private void seed(String company, String title, String location, String salary,
                       String skillsRequired, double minCgpa, String description) {
        if (jobRepository.existsByCompanyIgnoreCaseAndTitleIgnoreCase(company, title)) {
            return;
        }
        jobRepository.save(Job.builder()
                .title(title)
                .company(company)
                .location(location)
                .description(description)
                .salary(salary)
                .skillsRequired(skillsRequired)
                .minCgpa(minCgpa)
                .build());
    }
}
