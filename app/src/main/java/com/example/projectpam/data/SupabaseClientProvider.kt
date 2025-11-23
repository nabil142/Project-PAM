package com.example.projectpam.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {

    private const val SUPABASE_URL = "https://yurmdgtuyzqgthlybcpb.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl1cm1kZ3R1eXpxZ3RobHliY3BiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM4ODQ3ODYsImV4cCI6MjA3OTQ2MDc4Nn0.ZE5e7Kl0am6zX2soV5caKyEkYaUUS825ldgKAsSqzG4"

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
    }
}
